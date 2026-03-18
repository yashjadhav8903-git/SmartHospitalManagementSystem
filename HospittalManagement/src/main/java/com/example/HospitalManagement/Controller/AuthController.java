package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.*;
import com.example.HospitalManagement.RefreshTokenConfg.RefreshRequestDTO;
import com.example.HospitalManagement.SpringSecurity.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v5/auth")
public class AuthController {

    private final AuthService authService;

    // 1 Login (Controller ka kaam )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    // 1 signup( Controller ka kaam )
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signupRequestDTO){
        return ResponseEntity.ok(authService.signup(signupRequestDTO));
    }

    // RefreshToken Controller
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // Logout Controller
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequestDTO logoutRequestDTO){
        authService.logout(logoutRequestDTO.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}