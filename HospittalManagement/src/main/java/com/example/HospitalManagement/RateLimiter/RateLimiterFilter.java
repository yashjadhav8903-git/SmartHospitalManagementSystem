package com.example.HospitalManagement.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{

            // 1 .URI extract karta hai (Login/Signup/Booking?) ( uri --> Uniform Resource Identifier )
            String uri = request.getRequestURI();
            log.info("Incoming request: {}", request.getRequestURI());

            // 2 .extract ip Address (ip = Internet Protocal)
            String ip = request.getHeader("X-Forwarded-For");
            // check ip
            if(ip == null){
                ip = request.getRemoteAddr(); // getremoteAddr -> user ka direct IP nikal leta hai.
            }

            // 3 .Extract UserId from request
            String userId = request.getHeader("userId");
            // key must be userID , but same time ager UserId nahi hai toh key must be ip and uri
            String key = (userId != null) ? userId : ip + ":" + uri;

            // 4 .first time request Allowed
            boolean allowed = true;
            try {
                // login / signUp
                // 5 .If uri contain login and signup then allowed
                if (uri.contains("/login") || uri.contains("/signup")) {
                    // called rateLimiterService
                    allowed = rateLimiterService.isAllowed(
                            key + ": auth", 5, 1, RateIntervalUnit.MINUTES
                    );
                }

                // Booking
                // 6 .If uri contains book-Appointment
                else if (uri.contains("/book-Appointment")) {
                    // called rateLimiterService
                    allowed = rateLimiterService.isAllowed(
                            key + ": booking", 10, 1, RateIntervalUnit.SECONDS
                    );
                }
                ///---> if Redis crach or fallback rateLimiter not be shutDown
            } catch (Exception e){
                // then allowed request to controller
                allowed = true;
                log.error("Something wrong between in Redis RateLimiter-Filter :{} ", request);
                handlerExceptionResolver.resolveException(request,response,null,e);
            }

            log.info("RateLimit | IP: {} | URI: {} | Allowed: {}", ip, uri, allowed);

            // 7 .ager request limit or duration ko exced kr rahi hai toh notAllowed
            if(!allowed){
                log.warn("🚨 Rate limit exceeded! IP: {} tried to access: {}", ip, uri);
                // response status code to fronted
                response.setStatus(429);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                String message = String.format(
                        "{\"error\": \"Too many requests\", \"message\": \"Please try again after %d seconds\"}"
                );
                response.getWriter().write(message);

                /// IMP *** --> or yehi sehi return krna hai naki request ko aage jane dena hai
                return;
            }

            // 8 .ager bas kuch control me hai toh aage bado --> go to next 👍 ( green flag )
           filterChain.doFilter(request,response);

        } catch (Exception e) {
            log.error("Problem Come for RateLimiterFilter : {}" , request);
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}