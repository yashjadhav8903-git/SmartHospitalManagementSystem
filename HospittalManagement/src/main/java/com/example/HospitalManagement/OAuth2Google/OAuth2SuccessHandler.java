package com.example.HospitalManagement.OAuth2Google;

import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.LoginResponseDTO;
import com.example.HospitalManagement.SpringSecurity.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //Authentication Server (Google) se jo Provide Access Token milne ke baad Usko oAuth2AuthenticationToken me Convert krna.
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // Login kaha se kr raha ( Ex. Google,Github )
        String registrationId = token.getAuthorizedClientRegistrationId();

        // Us Token me se OAuth2 User Nikhalna hai
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        ResponseEntity<LoginResponseDTO> loginResponseDTOResponseEntity = authService.handleOAuth2LoginRequest(oAuth2User,registrationId);

        // return loginResponse to Frontent
        response.setStatus(loginResponseDTOResponseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(loginResponseDTOResponseEntity.getBody()));
    }
}