package com.example.HospitalManagement.Configuration;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Configurations {


    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    // password sefaty
    @Bean
    // Password Encoder BCryptpassword
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // login verify karna
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            // Java 8 Date/Time module register karna zaroori hai
            mapper.registerModule(new JavaTimeModule());
            // Dates ko timestamps (numbers) ki jagah ISO strings mein rakhne ke liye
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }

        //Redisson Configuration
        @Bean
        public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10);

        return Redisson.create(config);
        }


    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
