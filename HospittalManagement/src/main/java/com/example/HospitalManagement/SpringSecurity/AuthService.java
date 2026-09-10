package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.AddressInfo.Address;
import com.example.HospitalManagement.DTO.PatientsDTO.PatientSignUpRequestDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.SignUpRegisterResponseDTO;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.LoginRequestDTO;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.LoginResponseDTO;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.SignUpRequestDTO;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.SignUpResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.ExceptionHandling.DuplicateEmailIdResourceException;
import com.example.HospitalManagement.ExceptionHandling.PatientNotFoundException;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.RefreshTokenConfg.*;

import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.RoleRepository;
import com.example.HospitalManagement.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final PatientRepository patientRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthJwtUtil authJwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository  roleRepository;




    // 2. Login flow( match username and password )
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        String sanitizedUsername = loginRequestDTO.getUsername().toLowerCase().trim();

        try {

            // AuthenticationManager --> calls → CustomUserService.loadUserByUsername()
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(sanitizedUsername, loginRequestDTO.getPassword())
            );
            // get user in database
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            // get token from AuthJwtUtil
            String token = authJwtUtil.generateAccessToken(userEntity);
            // jwt token ke sath refresh token UUID me mile
            RefreshTokenRedisDTO refreshToken = refreshTokenService.CreateRefreshToken(userEntity.getUsername());

            log.info("User logged in successfully: {}", sanitizedUsername);
            // return jwtToken and userId
            return new LoginResponseDTO(token, refreshToken.getToken(),userEntity.getId());

        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for user: {}", sanitizedUsername);
            throw new BadCredentialsException("Invalid username or password");
        }


    }

    // 2 . userSign flow
    @Transactional
    public UserEntity signupInternal(SignUpRequestDTO signupRequestDTO, AuthProviderType authProviderType, String providerId) {
        String sanitizedUsername = signupRequestDTO.getUsername().toLowerCase().trim();

        // Check duplicate username
        if (userRepository.existsByUsername(sanitizedUsername)) {
            throw new DuplicateEmailIdResourceException("User with username " + sanitizedUsername + " already exists.");
        }

        // Default PATIENT role directly fetch karo DB se
        RoleEntity patientRole = roleRepository.findByRolesName(RolesType.PATIENT)
                .orElseThrow(() -> new IllegalArgumentException("Default PATIENT role not found in DB"));

        // 3. Agar nahi mila, toh naya user banao
        UserEntity userEntity = UserEntity.builder()
                .username(sanitizedUsername)
                .name(signupRequestDTO.getName())
                .providerType(authProviderType)
                .providerId(providerId)
                .roles(Set.of(patientRole))  //--> this is correct
                .build();

                // Sirf tab password encode karo jab providerType Email ho
        if(authProviderType == AuthProviderType.EMAIL && signupRequestDTO.getPassword() != null){
            userEntity.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        }

        // --> user ko save kiya kyu ager user hoga table toh patient banega na. he sign up as user than I decide his roles like Patient,Doctor or Admin
        userEntity = userRepository.save(userEntity);

        // return userEntity
        return userEntity;
    }


    // Controller method
    @Transactional
    public SignUpResponseDTO signup(SignUpRequestDTO signupRequestDTO){
        UserEntity userEntity = signupInternal(signupRequestDTO,AuthProviderType.EMAIL,null);
        return new SignUpResponseDTO(userEntity.getId(), userEntity.getUsername());
    }

    @Transactional
    public SignUpRegisterResponseDTO registerPatient (PatientSignUpRequestDTO requestDTO) {
        String sanitizedUsername = requestDTO.getEmail().toLowerCase().trim();

        // Duplicate check
        if(userRepository.existsByUsername(sanitizedUsername)) {
            throw new DuplicateEmailIdResourceException("User already exists with email: " + sanitizedUsername);
        }

        // Assign patient role
        RoleEntity patientRole = roleRepository.findByRolesName(RolesType.PATIENT)
                .orElseThrow(() -> new PatientNotFoundException("Role PATIENT not found in DB"));

        // 3. Create & Save UserEntity
        UserEntity userEntity = UserEntity.builder()
                .username(sanitizedUsername)
                .name(requestDTO.getName())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .providerType(AuthProviderType.EMAIL)
                .roles(Set.of(patientRole))
                .build();

        UserEntity userSaved = userRepository.save(userEntity);

        // add address
        Address addressSaved = Address.builder()
                .city(requestDTO.getCity())
                .addressLine(requestDTO.getAddressLine())
                .pincode(requestDTO.getPincode())
                .state(requestDTO.getState())
                .build();

        // 4. Create & Save Patient Profile
        Patient patient = Patient.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .birthdate(requestDTO.getBirthdate())
                .gender(requestDTO.getGender())
                .BloodGroup(requestDTO.getBloodGroup())
                .address(addressSaved)
                .userEntity(userSaved)
                .createdBy(userSaved) // Self-registered
                .build();

        patientRepository.save(patient);

        String name = requestDTO.getName();

        String text = "Hello " + name + ", welcome to the  family. " +
                "Your health and comfort are our top priorities. Wishing you a speedy recovery👩🏻‍⚕️";

        log.info("Patient onboarded successfully via direct registration: {}", sanitizedUsername);

        // 5. Tokens Generation after saving
        String accessToken = authJwtUtil.generateAccessToken(userSaved);
        RefreshTokenRedisDTO refreshToken = refreshTokenService.CreateRefreshToken(userSaved.getUsername());

        return new SignUpRegisterResponseDTO(
                userSaved.getId(),
                userSaved.getUsername(),
                accessToken,
                refreshToken.getToken(),
                text
        );
    }

    // Logout Flow
    @Transactional
    public void logout(String refreshToken){
        refreshTokenService.deleteToken(refreshToken);
    }


    @Transactional
    // Refresh token Rotation
    public LoginResponseDTO refresh(RefreshRequestDTO request) {

        // 1. frontend ne jo token bheja hai usko find krta hai . ager hai toh sahi varna exception throw krta hai.
        RefreshTokenRedisDTO oldToken = refreshTokenService.getTokenFromRedisOrDB(request.getRefreshToken());

        // 2. this token still valid or not
        refreshTokenService.verifyExpired(oldToken);
        // 🔄 Rotation (Ab ye hume naya DTO dega)
        RefreshTokenRedisDTO newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);

        // Fetch user linked to this token
        UserEntity user = userRepository.findByUsername(newRefreshToken.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found for token"));

        // 5. Generate NewAccessToken
        String NewAccessToken = authJwtUtil.generateAccessToken(user);
        // 6. User ko naya Access token aur wahi Purana Refresh token apps bhej diya jata hai.
        return new LoginResponseDTO(NewAccessToken, newRefreshToken.getToken(),user.getId());
    }



    // OAuth2 with Google ke methods
    // --> I use username field like email also. 👍❤️‍🩹
    @Transactional
    public LoginResponseDTO handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {

        //--> fetch AuthProviderType and ProviderId (Ex.Google,Github) kaha se login kiya hai.
        AuthProviderType authProviderType = authJwtUtil.getProviderTypeFromRegistrationId(registrationId);
        // --> ProviderId
        String providerId = authJwtUtil.getProviderIdFromOAuth2User(oAuth2User,registrationId);

        // lekin hamesha signup ke time email nahi milta .
        String determinedUsername = authJwtUtil.getDetermineUsernameAndEmailFormOAuth2user(oAuth2User,registrationId,providerId);  //--> find username or email

        //--> Kabhi kabuki humne email id bhi multi hai . Ager milti hai toh usko Save krlo 👍
        String email = oAuth2User.getAttribute("email");
        if(email != null) {
            email = email.toLowerCase().trim(); // --> case sensitive
        }

        // --> check this ProviderId and ProviderType ka user file se Database me hai kya ? ager nahi Create kro (// save AuthProviderType and ProviderId info with user )
        UserEntity userEntity = userRepository.findByProviderIdAndProviderType(providerId,authProviderType).orElse(null);

        // --> Same Email wala banda firse login nahi kr data fir wo Dusre Provider sahi kyu na ho ( Ager google ke ek eamil id se login kiya hai toh github se bhi us email id se login nahi kr sakta 🔥👍)
         //--> better and readable version
        UserEntity emailUser = (email != null && !email.isBlank()) ?
                userRepository.findByUsername(email).orElse(null) : null;


       // --> Ager email null or user (userEntity) bhi null he toh User new hai
        if(userEntity == null && emailUser == null){
            // SCENARIO A: Fresh User -> AAPKA `signupInternal` METHOD USE HO RAHA HAI!
            // then Signup first
            SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO(
                    determinedUsername,
                    null,  // --> password is null because ager provider google hai toh sirf email id se signup hoga use password ki array nhi.
                    oAuth2User.getAttribute("name")
            );
                userEntity = signupInternal(signUpRequestDTO,authProviderType ,providerId);
                log.info("New User created via signupInternal: {}", userEntity.getUsername());

            // Ager us time pr Email nahi mila jab wo signup kr raha tha . lekin baad me de email add kr krna chahta ho toh usko save kro
        } else if(userEntity == null && emailUser != null){
            // SCENARIO B: Account Linking (Email already bloack hone ki jagah link hoga)
            userEntity = emailUser;
            userEntity.setProviderId(providerId);
            userEntity.setProviderType(authProviderType);
            userRepository.save(userEntity);
            log.info("Linked provider {} to existing email: {}", authProviderType, email);

        } else {
            // SCENARIO C: Regular OAuth2 Returning User -> // email mila hai or wo email apke username se match nahi krta.
            if (email != null && !email.isBlank() && !email.equals(userEntity.getUsername())) {
                userEntity.setUsername(email);
                userRepository.save(userEntity);
            }
        }

        // tokens Generation
        String accessToken = authJwtUtil.generateAccessToken(userEntity);
        String refreshToken = refreshTokenService.CreateRefreshToken(userEntity.getUsername()).getToken();

        //Login
        return new LoginResponseDTO(accessToken,refreshToken,userEntity.getId());

    }
}