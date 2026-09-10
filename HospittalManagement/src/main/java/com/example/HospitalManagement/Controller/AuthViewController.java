package com.example.HospitalManagement.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Login with google & github")
public class AuthViewController {

    @Operation(
            summary = "OAuth2 Login UI Page",
            description = "<b>Open OAuth2 Login Page:</b> <a href='/login' target='_blank'>Click here to open Login Page</a>"
    )
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Thymeleaf src/main/resources/templates/login.html render karega
    }
}
