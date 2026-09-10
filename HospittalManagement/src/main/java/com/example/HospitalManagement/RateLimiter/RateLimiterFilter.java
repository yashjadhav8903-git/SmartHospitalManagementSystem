package com.example.HospitalManagement.RateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/doc");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        try{

            // 1 .URI extract karta hai (Login/Signup/Booking?) ( uri --> Uniform Resource Identifier )
            String uri = request.getRequestURI();
            log.info("Incoming request: {}", request.getRequestURI());

            // 2 .extract ip Address (ip = Internet Protocol)
           String ip = extractClientIp(request);

            // 3 .Extract UserId from request
            String userId = request.getHeader("userId");
            // key must be userID , but same time ager UserId nahi hai toh key must be ip and uri
            String key = (userId != null && !userId.isBlank()) ? "USER:" + userId : "IP:" + ip ;
            /**
             *  we already have userId and Ip address so we identify or each User have there personal bucket.
             *  100 patients agar 1 minute me aayenge toh system unhe block bilkul NAHI karega.
             */

            // 4 .first time request Allowed
            boolean allowed = true;

            try {
                // login / signUp
                // 5 .If uri contain login and signup then allowed
                if (uri.contains("/login") || uri.contains("/signup")) {
                    // called rateLimiterService
                    allowed = rateLimiterService.isAllowed(
                            key + ":AUTH", 5, 1, RateIntervalUnit.MINUTES
                    );
                }

                // Booking
                // 6 .If uri contains book-Appointment
                else if (uri.contains("/book-Appointment")) {
                    // called rateLimiterService
                    allowed = rateLimiterService.isAllowed(
                            key + ":BOOKING", 10, 1, RateIntervalUnit.SECONDS
                    );
                }
                else {
                    // 🛡 DEFAULT LIMIT: Baaki pure application ke har API endpoint ke liye
                    allowed = rateLimiterService.isAllowed(
                            key + ":GENERAL", 60, 1, RateIntervalUnit.MINUTES
                    );
                }
                ///---> if Redis crach or fallback rateLimiter not be shutDown
            } catch (Exception e){
                // Redis is down or network timeout occurred
                log.error("Redis RateLimiter error. Falling back to FAIL-OPEN: {}", e.getMessage());
                // FAIL-OPEN: Set allowed to true so the app keeps working even if Redis is dead
                allowed = true;
            }

            log.info("RateLimit | IP: {} | URI: {} | Allowed: {}", ip, uri, allowed);

            // 7 .ager request limit or duration ko exced kr rahi hai toh notAllowed
            if (!allowed) {
                log.warn("❌ Rate limit HIT | IP: {} | URI: {}", ip, uri);
                // response status code to fronted
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String jsonResponse = "{\"error\": \"Too Many Requests\", \"message\": \"Please try again later.\"}";
                response.getWriter().write(jsonResponse);

                /// IMP *** --> or yehi sehi return krna hai naki request ko aage jane dena hai
                return;
            } else {

                log.info("✅ Allowed | IP: {} | URI: {}", ip, uri);
            }

            // 8 .ager bas kuch control me hai toh aage bado --> go to next 👍 ( green flag )
           filterChain.doFilter(request,response);

        } catch (Exception e) {
            log.error("Unhandled Exception in RateLimiterFilter : {}" , request);
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }

    private String extractClientIp (HttpServletRequest request){
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}