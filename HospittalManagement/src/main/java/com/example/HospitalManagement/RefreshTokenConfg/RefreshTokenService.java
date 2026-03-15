package com.example.HospitalManagement.RefreshTokenConfg;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.SpringSecurity.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken CreateRefreshToken(String username){
        // 1. check in database . user not in database then throw exception.
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();

        // 2 -->
        RefreshToken refreshTokenBuild = RefreshToken.builder()
                .userEntity(userRepository.findByUsername(username).get())  // userEntity se user find kr liya
                .token(UUID.randomUUID().toString())  //token me ek random UUID Create kr di.
                .expiredDate(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 1))  // Token valid for 1 days
                .build();  //Bulid all of this
        return refreshTokenRepository.save(refreshTokenBuild); // save in refreshTokenRepository and return it.
    }

    @Transactional
    public RefreshToken verifyExpired(RefreshToken refreshToken){
        //1.check ager tokenExpiredDate aaj se aage nikal gayi hai .
        if(refreshToken.getExpiredDate().isBefore(Instant.now())){
            // ager aaj ki date se aage hai toh delete kr do.
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired"); // login again
        }
        // Ab sahi toh normally refeshToken return kr do.
        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setToken(UUID.randomUUID().toString());
        oldToken.setExpiredDate(Instant.now().plus(30, ChronoUnit.DAYS)); // example
        return refreshTokenRepository.save(oldToken);
    }
}