package com.example.HospitalManagement.ExceptionHanding;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.SignatureException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //1. JWT se aane wale MAIN exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException expiredJwtException){
        log.error("Exception Request belongs to jwtExpired: {}", expiredJwtException);
    ApiError apiError = new ApiError(HttpStatus.NOT_ACCEPTABLE,"Jwt Token Expired Exception");
    return new ResponseEntity<>(apiError,HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(MalformedJwtException malformedJwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"MalformedJwtException : Wrong Token format");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleSignatureException(SignatureException signatureException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,"Secret key not found");
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiError> handleUnsupportedJwtException(UnsupportedJwtException unsupportedJwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Jwt Algorithm not support");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"Try Another Username or Email");
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException jwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Invalid Jwt Token");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException){
        log.error("Exception Request Belongs to JWT: {}" , accessDeniedException);
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN,"Access denied : Insufficient permission");
        return new ResponseEntity<>(apiError,HttpStatus.FORBIDDEN);
    }


    // 2. Spring Security side ke

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handlerUsernameNotFoundException(UsernameNotFoundException ex){
        log.error("Exception Request Belongs to Security: {}", ex);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Username Not Found in Database");
        return new ResponseEntity<>(apiError,apiError.getStatusCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException badCredentialsException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Wrong Credentials");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException disabledException){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"User Disable : " + disabledException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ApiError> handleCredentialsExpiredException(CredentialsExpiredException credentialsExpiredException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Password incorrect | password Expire : " + credentialsExpiredException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }


    // 3. Database / System side

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccessException (DataAccessException dataAccessException){
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"Database down | Query fail : " + dataAccessException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException (EntityNotFoundException entityNotFoundException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,"Hibernate Error EntityNotFound : " + entityNotFoundException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiError> handleIOException(ServletException servletException){
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"Filter chain Issue : " + servletException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Database Errors (Duplicate user_id error ke liye)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException){
        ApiError apiError = new ApiError(HttpStatus.CONFLICT,"Database Error: Record already exists or constraint violation.");
        return new ResponseEntity<>(apiError,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException (NoSuchElementException noSuchElementException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,"Resource not found: Maybe you are already logged out?");
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

    // 4. OAuth2 Authentication Exception

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<ApiError> handleOAuth2AuthorizationException(OAuth2AuthorizationException oAuth2AuthorizationException){
        log.error("Exception Request Belongs to Auth2Authentication: {}", oAuth2AuthorizationException);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"OAuth2 authorization failed. Invalid or expired authorization request.");
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException( AuthenticationException authenticationException){
        log.error("Exception Request Belongs to Authentication: {}", authenticationException);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Authentication failed. Please check your credentials.");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClientAuthorizationRequiredException.class)
    public ResponseEntity<ApiError> handleClientAuthorizationRequiredException(ClientAuthorizationRequiredException clientAuthorizationRequiredException){
        log.error("Exception Request Belongs to ClientAuthorization: {}" ,clientAuthorizationRequiredException);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Client authorization required. Please login again.");
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    // 5 . Omniversal Exception ( GOAT of all Exceptions ) 🤣
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception){
        log.error("Fetching Data from Redis for Patient Page: {}", exception);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occcured : " + exception.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 4. Redis Exception
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiError> handleRedisConnectionFailureException(RedisConnectionFailureException redisConnectionFailureException){
        log.error("Exception Request Belongs to Redis: {}", redisConnectionFailureException);
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, " Redis not available ");
        return new ResponseEntity<>(apiError,HttpStatus.SERVICE_UNAVAILABLE);
    }
}
