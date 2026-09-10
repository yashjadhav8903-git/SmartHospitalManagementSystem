package com.example.HospitalManagement.Rabbit_MQ;

import com.example.HospitalManagement.Entity.AuditLog;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.AuditLogEventDTO;
import com.example.HospitalManagement.Repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDIT)
    public void consumeAuditLog(AuditLogEventDTO  eventDTO) {
    log.info("Consuming AuditLog Event for action : {} ",  eventDTO.getAction());


    AuditLog auditLog = AuditLog.builder()
            .action(eventDTO.getAction())
            .actor(eventDTO.getActor())
            .resource(eventDTO.getResource())
            .ipAddress(eventDTO.getIpAddress())
            .details(eventDTO.getDetails())
            .status(eventDTO.getStatus())
            .dateTime(eventDTO.getTimestamp())
            .build();

    auditLogRepository.save(auditLog);

    }
}
