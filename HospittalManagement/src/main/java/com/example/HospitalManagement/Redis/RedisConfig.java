package com.example.HospitalManagement.Redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Builder
public class RedisConfig {

    ///**Important Note --> RedisTemplate use for manual caching (ex.we Store Refreshtoken manually)
    @Bean
    RedisTemplate<String,Object> Template (RedisConnectionFactory connectionFactory){  //--> Redis Server ke sath Physical Connection ke liye RedisConnectionFactory
        // Store the key and value to redis ( key -> hamesha String , Object -> koi bhi Object ho sakta hai (User,Patient,String also etc)
        RedisTemplate <String,Object> redisTemplate = new RedisTemplate<>();

        // setConnection
        redisTemplate.setConnectionFactory(connectionFactory);

        /// --> ObjectMapper Setup (For Java 8 Dates) 1 - 3
        // 1. Create New ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 2. Java 8 Date/Time module register karo
        objectMapper.registerModule(new JavaTimeModule());
        // 3. Dates ko timestamps ki tarah likhna band karo (Readable banane ke liye)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 4. Typing enable karo (Isse LinkedHashMap wala error solve hoga)
        // Ye JSON mein "@class" property add karega taki Redis ko pata chale ye RefreshToken hai
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(), //--> Yeh ek security guard ki tarah hai. Yeh check karta hai ki jo class hum JSON mein likh rahe hain, wo safe hai ya nahi. (Taki koi hacker galat class insert karke tumhara system crash na kar de).
                ObjectMapper.DefaultTyping.NON_FINAL, //--> Yeh batata hai ki kaun-kaun si classes ki information JSON mein daalni hai. NON_FINAL ka matlab hai ki har wo class jo final nahi hai (jaise tumhari RefreshToken, User, Patient), unki details save karo.
                JsonTypeInfo.As.PROPERTY //--> Yeh batata hai ki class ka naam JSON mein kaise dikhega. PROPERTY ka matlab hai ki JSON ke andar ek naya field ban jayega, jiska default naam hota hai @class.
        );

        // 5. Ye naya mapper GenericSerializer ko dedo (✅ Genericserializer ko batana padega ki ye wala objectMapper use kare)
        GenericJackson2JsonRedisSerializer serializer =  new GenericJackson2JsonRedisSerializer(objectMapper);

        // 6. setKeySerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //--> Redis me key Data Plan String me Store hota hai

        // 7. SetValueSerializer
        redisTemplate.setValueSerializer(serializer);  //--> Redis ko nahi nahi pata "Java Object" toh Jackson library tumhare java object ko json  format me store krti hai.

        // 8. setHashKeySerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 9. setHashValueSerializer
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    ///**Important Note --> RedisCacheManager use for Auto caching (ex.Cacheable)
    @Bean
    public RedisCacheManager cacheManager (RedisConnectionFactory redisConnectionFactory){

        /// --> ObjectMapper Setup (For Java 8 Dates)
        // 1. create a new ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 2. Java 8 Date/Time module register karo
        objectMapper.registerModule(new JavaTimeModule());
        // 3. Dates ko timestamps ki tarah likhna band karo (Readable banane ke liye)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 4. Typing enable karo (Isse LinkedHashMap wala error solve hoga)
        // Ye JSON mein "@class" property add karega taki Redis ko pata chale ye RefreshToken hai
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(), //--> Yeh ek security guard ki tarah hai. Yeh check karta hai ki jo class hum JSON mein likh rahe hain, wo safe hai ya nahi. (Taki koi hacker galat class insert karke tumhara system crash na kar de).
                ObjectMapper.DefaultTyping.NON_FINAL, //--> Yeh batata hai ki kaun-kaun si classes ki information JSON mein daalni hai. NON_FINAL ka matlab hai ki har wo class jo final nahi hai (jaise tumhari RefreshToken, User, Patient), unki details save karo.
                JsonTypeInfo.As.PROPERTY //--> Yeh batata hai ki class ka naam JSON mein kaise dikhega. PROPERTY ka matlab hai ki JSON ke andar ek naya field ban jayega, jiska default naam hota hai @class.
        );

        // 5. Ye naya mapper GenericSerializer ko dedo (✅ Genericserializer ko batana padega ki ye wala objectMapper use kare)
        GenericJackson2JsonRedisSerializer serializer =  new GenericJackson2JsonRedisSerializer(objectMapper);

        //Agar kisi cache ka special config nahi mila ( Ager maine jo Add kiye hai Appointment,patient,doctor ke alawa koi or redis me hoga toh )
        RedisCacheConfiguration defualtCache = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))   // --> Jo dusra Data 10 minute h rahega
                .disableCachingNullValues()         // --> Ager DB se null data aa raha ho toh wo Redis Store nahi hoga.
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));  // java object -> JSON format me convert hoga , Redis me readable format me store hoga.

        // Humne bs Key-Value store krni hai bs isliye Hashmap Eazy to use hai.
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();  //--> Har cache ke liye alag TTL define kar raha hai

        // 1 .Auto TTL for Appointment
        configMap.put("appointments",
                defualtCache.entryTtl(Duration.ofMinutes(15)));  // 15 minutes ke baad redis se data delete hoga

        // 2 .Auto TTL Doctor
        configMap.put("doctors",
                defualtCache.entryTtl(Duration.ofHours(2)));     // 2 hours ke baad redis se data delete hoga


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defualtCache)  // --> // Default config for others
                .withInitialCacheConfigurations(configMap)  //--> let Spring boot know ki configMap ignore na kre .Jo specific cache settings tu ne map me define ki hai (appointments, patients, doctors), unko apply kar do”
                .build();
    }
}

///1.--> Problem : Jackson standard format mein Instant and LocalTimeDate ko serialise nahi kar paata. Jackson standard format mein Instant ko serialise nahi kar paata
///  --> solution 1 - 4
/// 2. --> Redis se data nikalte waqt Jackson ko pata nahi tha ki ye kya hai. Wo use LinkedHashMap bana raha tha. Jab tum use zabardasti (RefreshToken) mein badalne ki koshish kar rahe the, toh Java "ClassCastException" de raha tha.
///  --> Solution 4 - 5
