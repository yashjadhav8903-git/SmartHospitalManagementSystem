package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.PaymentsDTOs.CreateOrderRequestDTO;
import com.example.HospitalManagement.DTO.PaymentsDTOs.RazorpayOrderResponseDTO;
import com.example.HospitalManagement.DTO.PaymentsDTOs.VerifyPaymentRequestDTO;
import com.example.HospitalManagement.Entity.Appointment;
import com.example.HospitalManagement.Entity.Payment;
import com.example.HospitalManagement.Enums.PaymentStatus;
import com.example.HospitalManagement.ExceptionHandling.AppointmentNotFoundException;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;



    @Value("${razorpay.key_id}")
    private String razorpaykeyId;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;


    @Value("${razorpay.currency}")
    private String currency;



    @Transactional
    @AuditLog(action = "Payment_Order", resource = "Payment")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isAppointmentOwnerForPatient(#requestDTO.appointmentId,authentication.name)) ")
    public RazorpayOrderResponseDTO paymentOrder(CreateOrderRequestDTO requestDTO) throws RazorpayException {

        log.info("Request come to Payment Order Service with Appointment-ID : {}", requestDTO.getAppointmentId());

        // find appointment
        Appointment appointment = appointmentRepository.findById(requestDTO.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment Not Found with id : " +  requestDTO.getAppointmentId()));



        // Razorpay expect amount in paise (1 INR = 100 Paise)
        int amountInPaisa = requestDTO.getAmount().multiply(new BigDecimal("100")).intValue();

        // create JSON Object
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaisa);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_" + appointment.getId() + "_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = order.get("id");

        // build payment object
        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(requestDTO.getAmount())
                .razorpayOrderId(razorpayOrderId)
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .build();

        // save in DB
        paymentRepository.save(payment);

        // return DTO
        return RazorpayOrderResponseDTO.builder()
                .razorpayOrderId(razorpayOrderId)
                .amount(requestDTO.getAmount())
                .currency(currency)
                .appointmentId(appointment.getId())
                .razorpayKeyId(razorpaykeyId)
                .build();
    }

    @Transactional
    @AuditLog(action = "payment_verify",resource = "Payment")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isAppointmentOwnerForPatient(#requestDTO.appointmentId,authentication.name))")
    public String verifyPayment(VerifyPaymentRequestDTO requestDTO) {


        log.info("Request come to verifyPayment Service with Appointment-ID : {}", requestDTO.getAppointmentId());

        Payment payment = paymentRepository.findByRazorpayOrderId(requestDTO.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment record not found for Order ID: " + requestDTO.getRazorpayOrderId()));

        boolean isVerified = false;

        // 1. Interviewer / Frontend Bypass Check (YML secret bypass for smooth testing)
        if ("TEST_PASS".equals(requestDTO.getRazorpaySignature())) {
            log.warn("Payment verification bypassed using TEST_PASS token!");
            isVerified = true;
        }else {

        String payload = requestDTO.getRazorpayOrderId() + "|" + requestDTO.getRazorpayPaymentId();
        String generatedSignature = HmacUtils.hmacSha256Hex(razorpaySecret, payload);

            if (generatedSignature.equals(requestDTO.getRazorpaySignature())) {
                isVerified = true;
            }
        }

        if(isVerified) {

            // Payment Success Update
            payment.setRazorpayPaymentId(requestDTO.getRazorpayPaymentId());
            payment.setRazorpaySignature(requestDTO.getRazorpaySignature());
            payment.setPaymentStatus(PaymentStatus.PAYMENT_SUCCESS);
            paymentRepository.save(payment);
            log.info("Payment Successful Done | Appointment ID : {} ", requestDTO.getAppointmentId());

            // Call AppointmentService to confirm & send email
            appointmentService.confirmedAppointmentAfterPayment(payment.getAppointment().getId());



            return "Payment Verified Successfully & Appointment Confirmed ✅ " +
                    "Thank you . A confirmation receipt and details" +
                    " have been sent to your registered email/phone number.";


        } else {
            payment.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment Verification Failed! Invalid Signature.");
        }
    }
}
