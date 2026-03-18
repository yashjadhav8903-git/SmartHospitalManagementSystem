package com.example.HospitalManagement.Redis;

import com.example.HospitalManagement.RefreshTokenConfg.RefreshTokenRedisDTO;
import com.example.HospitalManagement.RefreshTokenConfg.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String ,Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String REDIS_PREFIX = "REFRESH_TOKEN:";
    // 1 --> Generate unique key and Add Prefix
    private String getKey(String token){
        return REDIS_PREFIX + token;
    }

    // 2 --> Save Token to Redis
    public void SaveToRedis(RefreshTokenRedisDTO refreshToken){
        String Key = getKey(refreshToken.getToken());

        redisTemplate.opsForValue().
                set(
                Key,
                refreshToken,
                Duration.between(Instant.now(),refreshToken.getExpiredDate())
        );
    }

    @Transactional
    // get Token from Redis
    public RefreshTokenRedisDTO getTokenFromRedis(String token){

        Object object = redisTemplate
                .opsForValue()
                .get(getKey(token));

        if(object == null){
            return null;
        }
        return (RefreshTokenRedisDTO) object;
    }

    // Delete from Redis
    public void deleteFromRedis(String token){
        String Key = getKey(token);
        redisTemplate.delete(Key);
    }


}
