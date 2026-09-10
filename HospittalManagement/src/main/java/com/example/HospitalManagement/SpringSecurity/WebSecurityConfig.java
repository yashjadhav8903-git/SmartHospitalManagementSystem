package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.OAuth2Google.OAuth2SuccessHandler;
import com.example.HospitalManagement.RateLimiter.RateLimiterFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)

public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final RateLimiterFilter rateLimiterFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(Csrf -> Csrf.disable())
                // Custom Login Page configure karo
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/v5/auth/login-success", true)
                )

                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v5/auth/**",
                                "/v5/patients/register",
                                "/oauth2/**",
                                "/login",
                                "/",
                                "/favicon.ico",
                                "/login/oauth2/**",
                                "/api/A2/schedule/**",
                                "/ping",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/doc/**",
                                "/doc"
                        ).permitAll()

                        .requestMatchers("/actuator/prometheus", "/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                                .requestMatchers("/secure").authenticated()

                        .anyRequest().authenticated()
                )

                // Custom JWT Filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimiterFilter,jwtAuthFilter.getClass())

                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/v5/auth/login-success", true))

                // Google / GitHub Login Configuration
                .oauth2Login(oAuth2 -> oAuth2.
                        failureHandler((request, response, exception) -> {
                            log.error("OAuth2 Error Type: {}, Message: {}",
                                    exception.getClass().getSimpleName(),
                                    exception.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            handlerExceptionResolver.resolveException(request,response,null,exception);
                        })
                        .successHandler(oAuth2SuccessHandler)
                )

                // 3. SMART EXCEPTION HANDLING (Browser vs Postman differentiation)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                // Postman/API Requests: Custom JSON Entry Point
                                (request, response, authException) ->
                                        handlerExceptionResolver.resolveException(request, response, null, authException),
                                // Step 1: Check karo ki kya client ne JSON request mangi hai?
                                new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                        )
                );
        return httpSecurity.build();
    }
}
