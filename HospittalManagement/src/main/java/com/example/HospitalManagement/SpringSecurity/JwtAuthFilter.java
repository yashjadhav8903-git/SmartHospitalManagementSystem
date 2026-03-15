package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.security.SignatureException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthJwtUtil authJwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            // 🔥 1️⃣ Refresh endpoint ko skip karo
            String requestPath = request.getServletPath();

            if (request.getServletPath().equals("/auth/refresh")) {
                filterChain.doFilter(request, response);
                return;
            }

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
            String token = requestTokenHeader.split("Bearer")[1].trim();

            // 5. Us Token se Username nikalta hai. authJwtutil se aata hai. ager username nikhal gaya matlab token humne hi generta kiya tha 😈
            String username = authJwtUtil.getUsernameFromToken(token);

            // 6. (1 -> Token se valid username mila?.  2 --> Kya is request ke liye pehle se koi user login toh nahi hai? (Authentication == null).
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 7. Agar token sahi hai, toh Database mein ja kar check krta hai username hamre DB me hai kya nhi
                UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
                // 8. Authentication Object Banana (Entry Pass) ( 1.userEntity -> allUserData 2.null -> Password( jarurat nhi token already verify ) 3. getAuthorities() --> User ki powers/roles
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());
                // 9. then usko SecurityContextHolder me save krte hai ( Aap kaun ho aur aapke paas kya permissions hain --> ab spring Security ko pata hai )
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            // 10. Request ko Aage Bhejna
            filterChain.doFilter(request, response);


            // All Exception's 🔥
        }
        catch (DisabledException disabledException){
            handlerExceptionResolver.resolveException(request,response,null,disabledException);  // User Disable
        }
        catch (BadCredentialsException badCredentialsException){
            handlerExceptionResolver.resolveException(request,response,null,badCredentialsException); // Wrong Password
        }
        catch (IllegalArgumentException illegalArgumentException){
            handlerExceptionResolver.resolveException(request,response,null,illegalArgumentException); // Empty Token | Null Tokon
        }
        catch (UnsupportedJwtException unsupportedJwtException) {
            handlerExceptionResolver.resolveException(request, response, null, unsupportedJwtException); // Jwt Algorithm not suppor
        }
        catch (MalformedJwtException malformedJwtException){  //
            handlerExceptionResolver.resolveException(request,response,null,malformedJwtException); // MalformedJwtException : Wrong Token format
        }
        catch (ExpiredJwtException expiredJwtException){ // Jwt Token Expired Exception
            handlerExceptionResolver.resolveException(request,response,null,expiredJwtException);
        }
        catch (JwtException jwtException){ //Jwt related
         handlerExceptionResolver.resolveException(request,response,null,jwtException);
        }
        catch (UsernameNotFoundException usernameNotFoundException){ // user nahi mila
            handlerExceptionResolver.resolveException(request,response,null,usernameNotFoundException);
        }
        catch (AuthenticationException authenticationException){  // Authentication fail
            handlerExceptionResolver.resolveException(request,response,null,authenticationException);
        }
        catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null,e); // Omniversal Exception ( GOAT of all Exception's )
        }
    }
}
