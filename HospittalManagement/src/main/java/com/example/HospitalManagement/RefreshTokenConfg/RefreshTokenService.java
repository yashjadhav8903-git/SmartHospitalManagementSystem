package com.example.HospitalManagement.RefreshTokenConfg;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Redis.RedisConfig;
import com.example.HospitalManagement.Redis.RedisService;
import com.example.HospitalManagement.SpringSecurity.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final RedisService redisService;
    private final RedisConfig redisConfig;

    @Transactional
    public RefreshTokenRedisDTO CreateRefreshToken(String username){
        // 1. check in database . user not in database then throw exception.
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        // 2 -->
        RefreshToken refreshTokenBuild = RefreshToken.builder()
                .userEntity(userRepository.findByUsername(username).get())  // userEntity se user find kr liya
                .token(UUID.randomUUID().toString())  //token me ek random UUID Create kr di.
                .expiredDate(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 1))  // Token valid for 1 days
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
        //1.check ager tokenExpiredDate aaj se aage nikal gayi hai .
        if(refreshToken.getExpiredDate().isBefore(Instant.now())){
            // delete redis token Also
            redisService.deleteFromRedis(refreshToken.getToken());
            // ager aaj ki date se aage hai toh delete kr do.
            refreshTokenRepository.deleteByToken(refreshToken.getToken());
            throw new RuntimeException("Refresh Token Expired"); // login again
        }
    }

    @Transactional
    public RefreshTokenRedisDTO rotateRefreshToken(RefreshTokenRedisDTO oldToken) {

        // 1. Sabse pehle purana token Redis se udaao 🚮
        redisService.deleteFromRedis(oldToken.getToken());
        System.out.println("🗑️ Deleted Old Token from Redis: " + oldToken.getToken());

        //2. DB se purana token nikalo
        RefreshToken ExistingToken = (RefreshToken) refreshTokenRepository.findBytoken(oldToken.getToken())
                .orElseThrow(() -> new RuntimeException("Token not found in DB"));

        //2. delete old redis token entry
//        redisService.deleteFromRedis(oldToken.getToken());

        // 3. User ko find karo (JWT claims ke liye UserEntity chahiye hi hogi)
        UserEntity user = userRepository.findById(oldToken.getId());

        // 3. Purani entity ki fields update karo (Isse duplicate error nahi aayega)
        ExistingToken.setToken(UUID.randomUUID().toString());
        ExistingToken.setExpiredDate(Instant.now().plus(30,ChronoUnit.DAYS));

        // 4. Save that in RefresToken DB
        RefreshToken updatedToken = refreshTokenRepository.save(ExistingToken);

        // 5. Entity -> DTO  Convrt kro
        RefreshTokenRedisDTO DTO  = mapToRedisDTO(updatedToken);
        // 6. DTO redis me save kro
        redisService.SaveToRedis(DTO);
        // 7. DTO return karo
        return DTO;
    }


    // Check RefreshToken in Redis then DB
    @Transactional
    public RefreshTokenRedisDTO getTokenFromRedisOrDB(String token) {

        // check token in redis
        RefreshTokenRedisDTO RedisToken = redisService.getTokenFromRedis(token);

        // check if token is null
        if (RedisToken != null) {
            System.out.println("RefreshToken Come form Redis : " + redisService.getTokenFromRedis(token));
            return RedisToken;  // --> Ager Redis me hai toh Return kr do
        }
        // Ager Redis me nahi hai toh DB se lao
        System.out.println("🏠 RefreshToken Come from Database");
        RefreshToken DBtoken = (RefreshToken) refreshTokenRepository.findBytoken(token).
                orElseThrow(() -> new RuntimeException("Invaild RefreshToken"));


        RefreshTokenRedisDTO dto = mapToRedisDTO(DBtoken);

        // save that DBToken in Redis
        redisService.SaveToRedis(dto);
        // return DTO
        return dto;
    }

    // logout / Delete Token
    @Transactional
    public void deleteToken(String token){

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