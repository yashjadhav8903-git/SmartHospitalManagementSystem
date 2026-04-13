package com.example.HospitalManagement.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RateIntervalUnit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{

            String uri = request.getRequestURI();

            String ip = request.getHeader("x-Forwed");
            if(ip == null){
                ip = request.getRemoteAddr();
            }

            String userId = request.getHeader("userId");
            String key = (userId != null) ? userId : ip;

            boolean allowed = true;

            // login / signUp
            if(uri.contains("/login") || uri.contains("/signup")){
                allowed = rateLimiterService.isAllowed(
                        key + ":auth",5,1, RateIntervalUnit.MINUTES
                );
                log.info("Rate limit remaining for IP :{}",ip);
            }

            // Booking
            else if (uri.contains("/book-Apppointment")) {
                allowed = rateLimiterService.isAllowed(
                        key + ": booking" , 10 ,1 ,RateIntervalUnit.SECONDS
                );
            }
            if(!allowed){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
                return;
            }

           filterChain.doFilter(request,response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
