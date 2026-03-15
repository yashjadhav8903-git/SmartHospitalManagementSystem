package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class AuthJwtUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1️⃣ Token Generate
    // 4. create token ager user exist krta hai toh
    public String generateAccessToken(UserEntity userEntity){
        return Jwts.builder()
                .setSubject(userEntity.getUsername())
                .claim("UserId",userEntity.getId().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*20))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Username nikalna ( login ke baad filter me )
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())  // --> verify krta hai token ke saath kisi ne chhed-chhad toh nahi ki
                .build()
                .parseSignedClaims(token)    // ---> Agar token mein space ho ya galat ho, toh yahi par MalformedJwtException aati hai.
                .getPayload();              // --> Token ke andar ki saari jankari (Claims) utha leta hai
        return claims.getSubject();         // --> Payload mein se "Subject" (jo ki hamara Username hai) return kar deta hai. ( joki generateAccessToken me setSubject hai )
    }





    // OAuth2 with Google ke methods
    // --> get AuthProviderType From registrationId
    public AuthProviderType getProviderTypeFromRegistrationId(String registrationId){
        return switch (registrationId.toLowerCase()){
            case "google" -> AuthProviderType.GOOGLE;
            case "github" -> AuthProviderType.GITHUB;
            case "amazon" -> AuthProviderType.AMAZON;
            case "facebook" -> AuthProviderType.FACEBOOK;
            default -> throw new IllegalArgumentException("Unsupported OAuth2 Provider : " + registrationId);
        };
    }


    // --> getProviderIdFromOAuth2user
    public String getProviderIdFromOAuth2User(OAuth2User oAuth2User,String registrationId){
        String providerId = switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "github" -> oAuth2User.getAttribute("id").toString();
            default -> {
                log.error("Unsupported OAuth2 privider :{} ", registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 privider "+ registrationId);
            }
        };
        // --> Ager ProviderId null or Blank hai
        if(providerId == null || providerId.isBlank()){
            log.error("Unable to determine ProviderId for Provider : {}", registrationId);
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }
        return providerId;
    }

    // --> Ager email mil gaya toh thik nahi toh us providertype ki value ues kr lenge
    public String getdetermineUsernameAndemailformOAuth2user(OAuth2User oAuth2User,String registrationId,String providerId){
        // --> Ager email mil gaya toh return email 👍
        String email = oAuth2User.getAttribute("email");
        if(email != null && !email.isBlank()){  // --> email null nahi or blank nahi hai
            return email;  // --> toh return email
        }
        // lekin ager nahi mila tohhhhh
        return switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");  // Google Sub value deta hai.
            case "github" -> oAuth2User.getAttribute("id");   // Github long value deta hai.
            default -> providerId;  // ager kuch nhi providerId kyu ki humne Empty Username or email save nahi krna hai 🔥
        };
    }
}