package com.example.HospitalManagement.AOPsAspect;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.AuditLogEventDTO;
import com.example.HospitalManagement.Rabbit_MQ.RabbitMQConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component

public class AuditAspect {

    private final RabbitTemplate rabbitTemplate;
    private final HttpServletRequest httpServletRequest;

    public AuditAspect(RabbitTemplate rabbitTemplate,HttpServletRequest httpServletRequest) {
        this.rabbitTemplate = rabbitTemplate;
        this.httpServletRequest = httpServletRequest;
    }

    @Around("@annotation(auditLog)")
    public Object AuditLogs(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable{

        long startTime = System.currentTimeMillis();

        // Current Authenticated User Fetch Karo
        String currentUser = "SYSTEM";
        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        String action = auditLog.action();
        String resource = auditLog.resource();
        String UserIpAddress = httpServletRequest.getRemoteAddr();
        Object result;

        try{

            // Method execute hone do
            result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            AuditLogEventDTO auditSuccessLogEventDTO = AuditLogEventDTO.builder()
                    .actor(currentUser)
                    .action(action)
                    .resource(resource)
                    .ipAddress(UserIpAddress)
                    .status("SUCCESS")
                    .details("Method Success : " + joinPoint.getSignature().getName())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_AUDIT,
                    auditSuccessLogEventDTO
            );

            System.out.println("Method Success Execution time : " + executionTime + "ms");

            return result;

        } catch (Throwable throwable) {

            long executionTime = System.currentTimeMillis() - startTime;
            // if failed

            AuditLogEventDTO auditLogFailedEventDTO = AuditLogEventDTO.builder()
                    .actor(currentUser)
                    .action(action)
                    .resource(resource)
                    .ipAddress(UserIpAddress)
                    .status("FAILED")
                    .details("Exception : " + throwable.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_AUDIT,
                    auditLogFailedEventDTO
            );

            System.err.println("Method Failed Execution time : " + executionTime + "ms");

            throw throwable;
        }

    }
}
