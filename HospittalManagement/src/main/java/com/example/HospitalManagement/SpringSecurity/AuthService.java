package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.LoginRequestDTO;
import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.LoginResponseDTO;
import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.SignUpRequestDTO;
import com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO.SignUpResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.Redis.RedisService;
import com.example.HospitalManagement.RefreshTokenConfg.*;
import com.example.HospitalManagement.Repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthJwtUtil authJwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PatientRepository patientRepository;
    private final RedisService redisService;

    // 2. Login flow( match username and password )
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {



            // AuthenticationManager --> calls → CustomUserService.loadUserByUsername()
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );
            // get user in database
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            // get token from AuthJwtUtil
            String Token = authJwtUtil.generateAccessToken(userEntity);
            // jwt token ke sath refreshtoken UUID me mile
            RefreshTokenRedisDTO refreshToken = refreshTokenService.CreateRefreshToken(userEntity.getUsername());


            // return jwtToken and userId
                return new LoginResponseDTO(Token,
                        refreshToken.getToken(),
                        userEntity.getId());
    }

    // 2 . userSign flow
    @Transactional
    public UserEntity signupInternal(SignUpRequestDTO signupRequestDTO, AuthProviderType authProviderType, String providerId) {

        // 1. Check karo ki user pehle se DB mein hai ya nahi
        UserEntity userEntity = userRepository.findByUsername(signupRequestDTO.getUsername()).orElse(null);

        // 2. Agar user mil gaya, toh signup nahi ho sakta
        if (userEntity != null) {
            throw new IllegalArgumentException("User Already Exists");
        }

        // 3. Agar nahi mila, toh naya user banao
        userEntity = UserEntity.builder()
                .username(signupRequestDTO.getUsername())
                .providerType(authProviderType)
                .providerId(providerId)
                //.roles(Set.of(RolesType.PATIENT)  //--> this is correct
                .roles(signupRequestDTO.getRoles())  // --> this for practice
                .build();

                // Sirf tab password encode karo jab providerType Email ho
                if(authProviderType == AuthProviderType.EMAIL){
                    userEntity.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
                }

        // --> user ko save kiya kyu ager user hoga tabhi toh patient banega na. he signup as user than i decide his roles like Patient,Doctor or Admin
        userEntity = userRepository.save(userEntity);

        // return userEntity
        return userEntity;
    }

    // Controller method
    @Transactional
    public SignUpResponseDTO signup(SignUpRequestDTO singupRequestDTO){
        UserEntity userEntity = signupInternal(singupRequestDTO,AuthProviderType.EMAIL,null);
        return new SignUpResponseDTO(userEntity.getId(), userEntity.getUsername());
    }

    // Logout Flow
    @Transactional
    public void logout(String refreshToken){
        refreshTokenService.deleteToken(refreshToken);
    }


    @Transactional
    // Refresh token Rotation
    public LoginResponseDTO refresh(RefreshRequestDTO request) {

        // 1. frontent ne jo token bheja hai usko find krta hai . ager hai toh sahi varna exception throw krta hai.
        RefreshTokenRedisDTO oldToken = refreshTokenService.getTokenFromRedisOrDB(request.getRefreshToken());

        // 2. this token still valid or not
        refreshTokenService.verifyExpired(oldToken);
        // 🔄 Rotation (Ab ye hume naya DTO dega)
        RefreshTokenRedisDTO newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        // 3. get user from oldToken
        // 3. UserEntity nikalne ka sahi tarika (Repository se fetch karo)
        // oldToken.getUsername() ya oldToken.getUserId() use karke DB hit karo
        UserEntity user = userRepository.findById(newRefreshToken.getId());
        // 🔥 LAZY FIX
//        user.getRoles().size();
        // 4. Rotate New RefreshToken
//        RefreshTokenRedisDTO newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        // 5. Generate NewAccessToken
        String NewAccessToken = authJwtUtil.generateAccessToken(user);
        // 6. User ko naya Access token aur wahi purana Refresh token wapas bhej diya jata hai.
        return new LoginResponseDTO(NewAccessToken, newRefreshToken.getToken(),user.getId());
    }





    // OAuth2 with Google ke methods
    // --> I use username field like email also. 👍❤️‍🩹
    @Transactional
    public ResponseEntity<LoginResponseDTO> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {

        //--> fetch AuthProviderType and ProviderId (Ex.Google,Github) kaha se login kiya hai.
        AuthProviderType authProviderType = authJwtUtil.getProviderTypeFromRegistrationId(registrationId);
        // --> ProviderId
        String providerId = authJwtUtil.getProviderIdFromOAuth2User(oAuth2User,registrationId);

        // --> check this ProviderId and ProviderType ka user pahile se Database me hai kya ? ager nahi Create kro (// save AuthProviderType and ProviderId info with user )
        UserEntity userEntity = userRepository.findByProviderIdAndProviderType(providerId,authProviderType).orElse(null);

        //--> Kabhi kabhi humne email id bhi milti hai . Ager milti hai toh usko Save krlo 👍
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        email = email.toLowerCase().trim();  // --> case sensitive

        // --> Same Email wala banda firse login nahi kr sakta fir wo Dusre Provider sahi kyu na ho ( Ager google ke ek eamil id se login kiya hai toh github se bhi us email id se login nahi kr sakta 🔥👍)
        //UserEntity emailUser = userRepository.findByUsername(email).orElse(null);  //--> normal
        //UserEntity emailUser = (email == null ) ? null : userRepository.findByUsername(email).orElse(null); // --> modify
         //--> better and readable version
        UserEntity emailUser = null;
        if (email != null && !email.isBlank()) {
            emailUser = userRepository.findByUsername(email).orElse(null);
        }

       // --> Ager email null or user (userEntity) bhi null he toh User new hai
        if(userEntity == null && emailUser == null){

            // then Signup first
            // lekin hamesha signup ke time email nahi milta .
            String username = authJwtUtil.getdetermineUsernameAndemailformOAuth2user(oAuth2User,registrationId,providerId);  //--> find username or email
            // get user
            userEntity = signupInternal(new SignUpRequestDTO(username, null, name,Set.of(RolesType.PATIENT)),authProviderType ,providerId); // --> password is null because ager provider google hai toh sirf email id se signup hoga usme password ki jarurat nhi.
                                                                                            // --> jab bhi OAuth2 ke flow se user ban raha hum use patient bana rahe hai.
            // Ager us time pr Email nahi mila jab wo signup kr raha tha . lekin baad me de email add kr krna chahta ho toh usko save kro
        } else if(userEntity != null){
            if(email != null && !email.isBlank() && !email.equals(userEntity.getUsername())){  // email mila hai or wo email apke username se match nahi krta.
             userEntity.setUsername(email);
             userRepository.save(userEntity);
            }
        } else {
            // ager apka user null hai or emailuser notNull hai tohh
            throw new BadCredentialsException("This email is already register with provider : " + emailUser.getProviderType());
        }

        // --> Refresh Token generate karo (Yahan 'email' hi username hai)
        String refreshToken = refreshTokenService.CreateRefreshToken(userEntity.getUsername()).getToken();

        //Login
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(authJwtUtil.generateAccessToken(userEntity),
                        refreshToken,userEntity.getId());

        return ResponseEntity.ok(loginResponseDTO);
    }
}