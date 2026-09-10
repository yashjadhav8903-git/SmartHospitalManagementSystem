package com.example.HospitalManagement.RefreshTokenConfg;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Redis.RedisService;
import com.example.HospitalManagement.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;


    // Standard Expiry Duration (e.g., 7 Days)
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    @Transactional
    public RefreshTokenRedisDTO CreateRefreshToken(String username){

        log.info("Creating Refresh Token for username: {}", username);

        // 1. check in database . user not in database then throw exception.
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()
                -> new EntityNotFoundException("User Not Found"));

        // 2 --> Build New Refresh Token Entity
        Instant expiryDate = Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);


        // 3 --> build token
        RefreshToken refreshTokenBuild = RefreshToken.builder()
                .userEntity(userEntity)  // userEntity se user find kr liya
                .token(UUID.randomUUID().toString())  //token me ek random UUID Create kr di.
                .expiredDate(expiryDate)  // Token valid for 7 day
                .build();  //Bulid all of this

        // save in refreshTokenRepository .
        RefreshToken savedToken = refreshTokenRepository.save(refreshTokenBuild);

        RefreshTokenRedisDTO dto = mapToRedisDTO(savedToken);
        // Saved to Redis
        redisService.SaveToRedis(dto);
        // return DTO
        return dto;
    }

    @Transactional
    public void verifyExpired(RefreshTokenRedisDTO refreshToken){
        // 1. Is the expiration time earlier than right now?" If yes, the token is dead/expired.
        if(refreshToken.getExpiredDate().isBefore(Instant.now())){
            log.warn("Token expired for token string: {}", refreshToken.getToken());
            // 2. delete redis token Also
            redisService.deleteFromRedis(refreshToken.getToken());
            // 3. ager aaj ki date se aage hai toh delete kr do.
            refreshTokenRepository.deleteByToken(refreshToken.getToken());
            throw new RuntimeException("Refresh Token Expired. Please login again!"); // login again
        }
    }

    @Transactional
    public RefreshTokenRedisDTO rotateRefreshToken(RefreshTokenRedisDTO oldToken) {
        log.info("Rotating Refresh Token for User ID: {}", oldToken.getId());

        // 1. First, verify if old token is expired
        verifyExpired(oldToken);

        // 2. Sabse pehle purana token Redis se udaao 🚮
        redisService.deleteFromRedis(oldToken.getToken());

        //3. DB se purana token nikalo
        RefreshToken ExistingToken = (RefreshToken) refreshTokenRepository.findByToken(oldToken.getToken())
                .orElseThrow(() -> {
                    // Agar Token Redis mein to DTO ban ke aaya par DB mein nahi mila, matlub token tampered ya hijacked hai!
                    log.error("SECURITY ALERT: Invalid or compromised token rotation attempt!");
                    return new EntityNotFoundException("Token not found in DB");
                });


        // 4. Mutate fields (In-place update for Dirty Checking / No duplicate insert)

        Instant newExpiryDate = Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS,ChronoUnit.DAYS);
        ExistingToken.setToken(UUID.randomUUID().toString());
        ExistingToken.setExpiredDate(newExpiryDate);

        // 4. Save that in RefresToken DB
        RefreshToken updatedToken = refreshTokenRepository.save(ExistingToken);

        // 5. Entity -> DTO  Convrt kro
        RefreshTokenRedisDTO newDTO  = mapToRedisDTO(updatedToken);
        // 6. DTO redis me save kro
        redisService.SaveToRedis(newDTO);
        // 7. DTO return karo
        log.info("Refresh Token rotated successfully for user: {}", newDTO.getUsername());
        return newDTO;
    }


    // Check RefreshToken in Redis then DB
    @Transactional
    public RefreshTokenRedisDTO getTokenFromRedisOrDB(String token) {

        // check token in redis
        RefreshTokenRedisDTO RedisToken = redisService.getTokenFromRedis(token);

        // check if token is null
        if (RedisToken != null) {
            log.info("RefreshToken Hit from Redis");
            return RedisToken;  // --> Ager Redis me hai toh Return kr do
        }
        // Ager Redis me nahi hai toh DB se lao
        log.info("RefreshToken Miss -> Fetching from Database");
        RefreshToken DBtoken = (RefreshToken) refreshTokenRepository.findByToken(token).
                orElseThrow(() -> new RuntimeException("Invalid Refresh-Token"));

        // Map to DTO and Cache Back in Redis
        RefreshTokenRedisDTO dto = mapToRedisDTO(DBtoken);

        //** Expiry check before caching DB result back to Redis
        verifyExpired(dto);

        // save that DBToken in Redis
        redisService.SaveToRedis(dto);
        // return DTO
        return dto;
    }

    // logout / Delete Token
    @Transactional
    public void deleteToken(String token){
        log.info("Revoking Refresh Token: {}", token);

        // delete from redis
        redisService.deleteFromRedis(token);
        // delete from Database also
        refreshTokenRepository.deleteByToken(token);
    }

    private RefreshTokenRedisDTO mapToRedisDTO(RefreshToken refreshToken){
        return new RefreshTokenRedisDTO(
                refreshToken.getUserEntity().getId(),
                refreshToken.getToken(),
                refreshToken.getExpiredDate(),
                refreshToken.getUserEntity().getUsername()
        );
    }
}