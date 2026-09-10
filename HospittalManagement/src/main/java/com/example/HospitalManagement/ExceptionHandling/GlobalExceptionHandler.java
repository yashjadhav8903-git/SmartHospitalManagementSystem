package com.example.HospitalManagement.ExceptionHandling;
import com.example.HospitalManagement.ExceptionHandling.ApiError;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //1. JWT se aane wale MAIN exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleExpiredJwtException(ExpiredJwtException ex,
                                                                           HttpServletRequest request){
        log.error("Exception Request belongs to jwtExpired: {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                "Jwt token Expired. " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(exceptionErrorRsponse);

    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleMalformedJwtException(MalformedJwtException ex ,
                                                                             HttpServletRequest request){

        log.error("Exception Request belongs to MalformedJwtException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleSignatureException(SignatureException ex,
                                                                          HttpServletRequest request){

        log.error("Exception Request belongs to SignatureException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleUnsupportedJwtException(UnsupportedJwtException ex,
                                                                               HttpServletRequest request){

        log.error("Exception Request belongs to UnsupportedJwtException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionErrorRsponse);

    }

    @ExceptionHandler(DuplicateEmailIdResourceException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleDuplicateResourceException(DuplicateEmailIdResourceException ex,
                                                                                  HttpServletRequest request){

        log.error("Exception Request belongs to DuplicateEmailIdResourceException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                  HttpServletRequest request){

        log.error("Exception Request belongs to MethodArgumentNotValidException : {}", ex.getMessage());

        Map<String,String> fieldError = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach( error -> fieldError.put(error.getField(),
                        error.getDefaultMessage()));

        ValidationExceptionResponseDTO exceptionErrorRsponse = new ValidationExceptionResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required Field Validation Error." + ex.getMessage(),
                request.getRequestURI(),
                fieldError

        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handlePatientNotFoundException(PatientNotFoundException ex,
                                                                                HttpServletRequest request){

        log.error("Exception Request belongs to PatientNotFoundException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }


    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleAppointmentNotFoundException(AppointmentNotFoundException ex,
                                                                                    HttpServletRequest request){
        log.error("Exception Request belongs to AppointmentNotFoundException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }


    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleDoctorNotFoundException(DoctorNotFoundException ex,
                                                                               HttpServletRequest request){
        log.error("Exception Request belongs to DoctorNotFoundException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }


    @ExceptionHandler(DoctorException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleDoctorException(DoctorException ex,
                                                                       HttpServletRequest request){
        log.error("Exception Request belongs to DoctorException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exceptionErrorRsponse);
    }



    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleJwtException(JwtException ex,
                                                                    HttpServletRequest request){
        log.error("Exception Request belongs to JwtException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleAccessDeniedException(AccessDeniedException accessDeniedException,
                                                                             HttpServletRequest request){
        log.error("Exception Request Belongs to AccessDeniedException : {}" , accessDeniedException.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exceptionErrorRsponse);
    }


    // 2. Spring Security side ke

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handlerUsernameNotFoundException(UsernameNotFoundException ex,
                                                                                  HttpServletRequest request){
        log.error("Exception Request Belongs to Security - UsernameNotFoundException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);

    }

    @ExceptionHandler(InsuranceNotFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handlerInsuranceNotFoundException(InsuranceNotFoundException ex,
                                                                                   HttpServletRequest request){

        log.error("Exception Request Belongs to InsuranceNotFoundException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(InsuranceExpiredException.class)
    public ResponseEntity<ExceptionErrorRsponse> handlerInsuranceExpiredException(InsuranceExpiredException ex,
                                                                                   HttpServletRequest request){

        log.error("Exception Request Belongs to InsuranceExpiredException : {}", ex.getMessage());

        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(NoActiveInsurancePolicyFoundException.class)
    public ResponseEntity<ExceptionErrorRsponse> handlerNoActiveInsurancePolicyFoundException(NoActiveInsurancePolicyFoundException ex,
                                                                                              HttpServletRequest request){

        log.error("Exception Request Belongs to NoActiveInsurancePolicyFoundException : {}", ex.getMessage());
        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex,
                                                                  HttpServletRequest request){
        log.error("Exception Request Belongs to Security - BadCredentialsException : {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Wrong Credentials" + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(apiError);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException disabledException,
                                                            HttpServletRequest request){
        log.error("Exception Request Belongs to Security - DisabledException : {}", disabledException.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "User Disable : " + disabledException.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleCredentialsExpiredException(CredentialsExpiredException ex,
                                                                                   HttpServletRequest request){
        log.error("Exception Request Belongs to Security - CredentialsExpiredException : {}", ex.getMessage());


        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Password incorrect | password Expire : " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionErrorRsponse);


    }


    // 3. Database / System side

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccessException (DataAccessException dataAccessException,
                                                               HttpServletRequest request){
        log.error("Exception Request Belongs to Database - DataAccessException : {}", dataAccessException.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database down | Query fail : " + dataAccessException.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException (EntityNotFoundException entityNotFoundException,
                                                                   HttpServletRequest request){
        log.error("Exception Request Belongs to Database - EntityNotFoundException : {}", entityNotFoundException.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Hibernate Error EntityNotFound : " + entityNotFoundException.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiError> handleIOException(ServletException servletException,
                                                      HttpServletRequest request){
        log.error("Exception Request Belongs to Database - ServletException : {}", servletException.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Filter chain Issue : " + servletException.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Database Errors (Duplicate user_id error ke liye)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                                          HttpServletRequest request){
        log.error("Exception Request Belongs to Database - DataIntegrityViolationException : {}", ex.getMessage());


        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                "Database Error: Record already exists or constraint violation." + ex,
                request.getRequestURI());
        return new ResponseEntity<>(apiError,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionErrorRsponse> handleNoSuchElementException (NoSuchElementException ex,
                                                                               HttpServletRequest request){
        log.error("Exception Request Belongs to Database - NoSuchElementException : {}", ex.getMessage());


        ExceptionErrorRsponse exceptionErrorRsponse = new ExceptionErrorRsponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Resource not found: Maybe you are already logged out ? " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionErrorRsponse);

    }


    // 4. OAuth2 Authentication Exception

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<ApiError> handleOAuth2AuthorizationException(OAuth2AuthorizationException oAuth2AuthorizationException,
                                                                       HttpServletRequest request){
        log.error("Exception Request Belongs to OAuth2Authentication: {}", oAuth2AuthorizationException.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "OAuth2 authorization failed. Invalid or expired authorization request." + oAuth2AuthorizationException,
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException( AuthenticationException authenticationException,
                                                                   HttpServletRequest request){

        log.error("Exception Request Belongs to Authentication: {}", authenticationException.getMessage());
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please check your credentials." + authenticationException,
                request.getRequestURI());

        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClientAuthorizationRequiredException.class)
    public ResponseEntity<ApiError> handleClientAuthorizationRequiredException(ClientAuthorizationRequiredException clientAuthorizationRequiredException,
                                                                               HttpServletRequest request){
        log.error("Exception Request Belongs to ClientAuthorization: {}" ,clientAuthorizationRequiredException.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Client authorization required. Please login again." + clientAuthorizationRequiredException,
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    // 4. Redis Exception
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiError> handleRedisConnectionFailureException(RedisConnectionFailureException redisConnectionFailureException,
                                                                          HttpServletRequest request){
        log.error("Exception Request Belongs to Redis: {}", redisConnectionFailureException.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE,
                " Redis not available." + redisConnectionFailureException,
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 5 . Omniversal Exception ( GOAT of all Exceptions ) 🤣
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception,
                                                           HttpServletRequest request){

        log.error("Exception Request Belongs to Application, Something went Wrong... : "  + exception.getMessage());

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occcured : " + exception.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
