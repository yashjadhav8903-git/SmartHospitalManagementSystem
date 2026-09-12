package com.example.HospitalManagement.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Payment UI (Recommended)")
public class PaymentViewController {

    @Operation(
            summary = "Open Secure Payment Page",
            description = "<b>Open Payment Gateway:</b> <a href='/index.html?appointmentId=1&amount=500' target='_blank'>Click here to open Payment Checkout</a>"
    )
    @GetMapping("/payment-ui")
    public String showPaymentPage() {
        return "forward:/index.html"; // Static folder wali index.html ko direct forward kar dega
    }
}
