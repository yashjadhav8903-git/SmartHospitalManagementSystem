package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthJwtUtil authJwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;


    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    // 1. Un saare endpoints ki list jo public hain
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/doc/**",
            "/",
            "/v5/auth/refresh",
            "/v5/auth/signup",
            "/v5/auth/login",
            "/v5/patients/register",
            "/oauth2/**",
            "/login/oauth2/**"

    );

    // 🔥 1️⃣ Refresh endpoint ko skip karo
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

            String path = request.getServletPath();
            // Check karo ki incoming path humari excluded list me hai ya nahi

            return EXCLUDED_PATHS.stream()
                    .anyMatch(excludePath -> pathMatcher.match(excludePath, path));

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            // 1. Frontent Incoming request log's ( Request Tracking )
            log.info("Incoming request: {}", request.getRequestURI());

            // 2. Frontent wale jwtheader se token nikalna ( "Main ye user hoon aur ye mera pass (token) hai" )
            final String requestTokenHeader = request.getHeader("Authorization");

            // 3. Check toke null toh nahi ? Aur kya wo "bearer" se start ho raha hai?.
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response); // --> Ager fail hua: Agar token nahi hai, toh doFilter karke request ko aage bhej deta hai. Server samajh jata hai ki ye "Public" API (jaise Login/Signup) ho sakti hai.
                return;
            }

            // 4. Token ko saaf krti hai ( sirf asli JWT Token ko token variable mein save karti hai. ) ex . "Bearer yfeeuygkue" Bearer is 0 and token is 1
            String token = requestTokenHeader.substring(7).trim();

            if(token.isEmpty()){
                filterChain.doFilter(request, response);
                return;
            }

            // 5. Us Token se Username nikalta hai. authJwtutil se aata hai. ager username nikhal gaya matlab token humne hi generta kiya tha 😈
            String username = authJwtUtil.getUsernameFromToken(token);

            // 6. (1 -> Token se valid username mila?.  2 --> Kya is request ke liye pehle se koi user login toh nahi hai? (Authentication == null).
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 7. Agar token sahi hai, toh Database mein ja kar check krta hai username hamre DB me hai kya nhi
//                UserEntity userEntity = userRepository.findByUsername(username).orElse(null);

                // get role and permission form jwtToken
                List<GrantedAuthority> authorities = authJwtUtil.getAuthoritiesFromToken(token);
               // check userEntity
//                if(userEntity != null){
                    // 8. Authentication Object Banana (Entry Pass) ( 1.userEntity -> allUserData 2.null -> Password( jarurat nhi token already verify ) 3. getAuthorities() --> User ki powers/roles
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    // 9. then usko SecurityContextHolder me save krte hai ( Aap kaun ho aur aapke paas kya permissions hain --> ab spring Security ko pata hai )
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                //}
            }
            // 10. Request ko Aage Bhejna
            filterChain.doFilter(request, response);


            // All Exception's 🔥
        } catch (JwtException | AuthenticationException e) { //Jwt related
            log.error("Authentication failed: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }catch (Exception e) {
            log.error("Unexpected Filter Exception from JWT filter : ", e);
            handlerExceptionResolver.resolveException(request, response, null, e); // Omniversal Exception ( GOAT of all Exception's )
        }
    }
}