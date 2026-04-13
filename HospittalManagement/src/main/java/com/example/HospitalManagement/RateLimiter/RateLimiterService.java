package com.example.HospitalManagement.RateLimiter;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedissonClient redissonClient;

    public boolean isAllowed (String key, long limit, long duration, RateIntervalUnit unit){

        RRateLimiter limiter = redissonClient.getRateLimiter("rateLimiter:" + key);

        if(!limiter.isExists()){
            limiter.trySetRate(RateType.OVERALL,limit,duration,unit);
        }
        return limiter.tryAcquire();
    }
}
