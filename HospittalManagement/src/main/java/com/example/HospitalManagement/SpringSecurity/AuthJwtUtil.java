package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        // 1. get role from list(userEntity)
        List<String> roles = userEntity.getRoles()
                .stream()
                .map(role ->role.getRolesName().name())
                .toList();

        // 2. and also get permission from userEntity
        List<String> permissions = userEntity.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getPermissionName().getPermissions())
                .distinct()
                .toList();

        return Jwts.builder()
                .subject(userEntity.getUsername())
                .claim("UserId", userEntity.getId().toString())
                .claim("Roles", roles)
                .claim("Permissions", permissions) // Fine-grained permissions inside JWT!
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Username nikalna ( login ke baad filter me )
    public String getUsernameFromToken(String token) {
       return getClaimsFromToken(token).getSubject();
    }

    // UserId Extract
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("UserId", String.class);
    }




    // Filter ke Zero DB Hit Authoritative Parsing for Authorities
    @SuppressWarnings("unckecked")
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {

        Claims claims = getClaimsFromToken(token);

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = claims.get("Roles", List.class);
        if(roles != null){
            roles
                    .forEach(role -> {
                        // Agar role me pehle se "ROLE_" laga hai toh wahi rehne do, warna add karo
                        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        authorities.add(new SimpleGrantedAuthority(roleName));
                    }

            );
        }

        List<String> permissions = claims.get("Permissions", List.class);
        if(permissions != null){
            permissions.forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission))
            );
        }

        return authorities;

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
    public String getDetermineUsernameAndEmailFormOAuth2user(OAuth2User oAuth2User,String registrationId,String providerId){
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