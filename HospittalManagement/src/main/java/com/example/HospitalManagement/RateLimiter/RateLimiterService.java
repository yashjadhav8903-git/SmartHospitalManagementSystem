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
    // request Allowed -> key -> ( UserId and must be uri + ip ) --> limit -> ( request Count ) ---> duration -->( kitne time ke allow hai ---> time unit(Minute /Second)
    public boolean isAllowed (String key, long limit, long duration, RateIntervalUnit unit){

        // RRateLimiter ko object chahiye hota hai redissonClient se . object means Key
        RRateLimiter limiter = redissonClient.getRateLimiter("rate::Limiter:" + key);

        // then check key/limiter.
        if(!limiter.isExists()){
            // ager limiter not exists then set this rule instent of old rule
            limiter.trySetRate(RateType.OVERALL,limit,duration,unit);
        }
        // Permission check "Kya main is request ko jaane doon?"
        return limiter.tryAcquire();
    }
}
// --> limiter.tryAcquire() ---> 1 .Redisson/limiter se permission mangta hai ki "Kya main is request ko jaane doon?" .
//                               2 .Ye step Redis me check krta hai ki bucket me token bacha hai ya nahi
//                               3 .If token Available --> Yes (1 token minus kro)
//                               4 .If token NotAvailble --> No (limit khatam)