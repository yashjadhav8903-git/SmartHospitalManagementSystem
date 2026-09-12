package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.PaymentsDTOs.CreateOrderRequestDTO;
import com.example.HospitalManagement.DTO.PaymentsDTOs.RazorpayOrderResponseDTO;
import com.example.HospitalManagement.DTO.PaymentsDTOs.VerifyPaymentRequestDTO;
import com.example.HospitalManagement.Service.PaymentService;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payments")
@Tag(name = "Payment-API's")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    @Operation(summary = "Initiate the  Payment Transaction.")
    public ResponseEntity<RazorpayOrderResponseDTO> createOrder(@RequestBody CreateOrderRequestDTO
                                                                        createOrderRequestDTO) throws RazorpayException {

        log.info("createOrder request {}", createOrderRequestDTO);

        RazorpayOrderResponseDTO razorpayOrderResponseDTO =
                paymentService.paymentOrder(createOrderRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(razorpayOrderResponseDTO);

    }


    @PostMapping("/verify")
    @Operation(summary = "NOTE : please enter razorpayPaymentId : pay_test_123456 and razorpaySignature : TEST_PASS")
    public ResponseEntity<String> verifyPayment(@RequestBody VerifyPaymentRequestDTO
                                                         verifyPaymentRequestDTO){

        log.info("verifyPayment request {}", verifyPaymentRequestDTO);

        String response =
                paymentService.verifyPayment(verifyPaymentRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }
}
