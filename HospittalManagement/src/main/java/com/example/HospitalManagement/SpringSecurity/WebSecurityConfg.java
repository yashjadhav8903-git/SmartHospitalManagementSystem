package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Enums.PermissionType;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.OAuth2Google.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebSecurityConfg {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(CsrfConfigurer -> CsrfConfigurer.disable())
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v5/auth/**").permitAll()
                                .requestMatchers("/api/A2/schedule/**").permitAll()
                                .requestMatchers("/ping").permitAll()
                                .requestMatchers("/secure").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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
                .exceptionHandling(ExceptionHandlingConfigurer ->
                        ExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException)
                                -> handlerExceptionResolver.resolveException(request,response,null,accessDeniedException)));
                //.formLogin(Customizer.withDefaults()); //Iska matlab hai ki aapne Default Login Form enable kar diya hai. matlab apne logout bhi kiya ho toh fir bhi isko access kr paoge kyu oo public hoti hai API's
        return httpSecurity.build();
    }
}
