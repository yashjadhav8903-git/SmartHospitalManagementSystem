package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.SpringSecurityDTO.*;
import com.example.HospitalManagement.RefreshTokenConfg.RefreshRequestDTO;
import com.example.HospitalManagement.SpringSecurity.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v5/auth")
@Tag(name = "User Authentication-API's")
public class AuthController {

    private final AuthService authService;

    // 1 Login (Controller ka kaam )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        log.info("Login Request Received for from : {}", loginRequestDTO);
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
    // 1 signup( Controller ka kaam )
    @PostMapping("/signup")
    @Operation(summary = "signup and login for book-Appointment.")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signupRequestDTO){
        log.info("Signup Request Received form : {}", signupRequestDTO);
        return ResponseEntity.ok(authService.signup(signupRequestDTO));
    }

    // RefreshToken Controller
    @PostMapping("/refresh")
    @Operation(summary = "For Refresh-Token generation.")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        log.info("Refresh Request Received from : {}", request);
        return ResponseEntity.ok(authService.refresh(request));
    }

    // Logout Controller
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequestDTO logoutRequestDTO){
        log.info("Logout Request received form : {}" , logoutRequestDTO);
        authService.logout(logoutRequestDTO.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}