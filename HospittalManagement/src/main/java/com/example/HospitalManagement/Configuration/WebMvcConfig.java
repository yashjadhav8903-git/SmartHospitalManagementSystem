package com.example.HospitalManagement.Configuration;

import com.example.HospitalManagement.Interceptor.PerformanceLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public PerformanceLoggingInterceptor performanceLoggingInterceptor(){
        return new PerformanceLoggingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor((performanceLoggingInterceptor()))
                .addPathPatterns("/v5/**")
                .excludePathPatterns("/v5/auth/login","/favicon.ico");
    }
}
