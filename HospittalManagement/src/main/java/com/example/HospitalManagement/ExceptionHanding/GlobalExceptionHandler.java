package com.example.HospitalManagement.ExceptionHanding;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import org.springframework.dao.DataAccessException;
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

@RestControllerAdvice
public class GlobalExceptionHandler {

    //1. JWT se aane wale MAIN exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException expiredJwtException){
    ApiError apiError = new ApiError(HttpStatus.NOT_ACCEPTABLE,"Jwt Token Expired Exception : " + expiredJwtException.getMessage());
    return new ResponseEntity<>(apiError,HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(MalformedJwtException malformedJwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"MalformedJwtException : Wrong Token format : " + malformedJwtException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleSignatureException(SignatureException signatureException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,"Secret key not found : " + signatureException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiError> handleUnsupportedJwtException(UnsupportedJwtException unsupportedJwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Jwt Algorithm not support : " + unsupportedJwtException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"Try Another Username or Email : " + illegalArgumentException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException jwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Invalid Jwt Token : " + jwtException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN,"Access denied : Insufficient permission" + accessDeniedException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.FORBIDDEN);
    }


    // 2. Spring Security side ke

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handlerUsernameNotFoundException(UsernameNotFoundException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Username Not Found in Database : " + ex.getMessage());
        return new ResponseEntity<>(apiError,apiError.getStatusCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException badCredentialsException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Wrong Credentials : " + badCredentialsException.getMessage());
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


    // 4. OAuth2 Authentication Exception

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ApiError> handleOAuth2AuthenticationException(OAuth2AuthenticationException oAuth2AuthenticationException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED ,"OAuth2 authentication failed. Please try again" + oAuth2AuthenticationException.getError());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<ApiError> handleOAuth2AuthorizationException(OAuth2AuthorizationException oAuth2AuthorizationException){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"OAuth2 authorization failed. Invalid or expired authorization request." + oAuth2AuthorizationException.getError());
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException( AuthenticationException authenticationException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Authentication failed. Please check your credentials. " + authenticationException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClientAuthorizationRequiredException.class)
    public ResponseEntity<ApiError> handleClientAuthorizationRequiredException(ClientAuthorizationRequiredException clientAuthorizationRequiredException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,"Client authorization required. Please login again." + clientAuthorizationRequiredException.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    // 5 . Omniversal Exception ( GOAT of all Exceptions ) 🤣
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception){
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occcured : " + exception.getMessage());
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
