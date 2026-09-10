package com.example.HospitalManagement.Rabbit_MQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;

/**
 * EXCHANGE, QUEUE & BINDING CLASS (Infrastructure Setup)  -> Exchange & Queue Definitions
 */


@Configuration
public class RabbitMQConfig {


    public static final String EXCHANGE = "booking_exchange";

    // Confirmed message properties
    public static final String QUEUE_CONFIRMED = "email_queue";
    public static final String ROUTING_KEY_CONFIRMED = "booking.confirmed";

    // Canceled message properties ( same exchange )
    public static final String QUEUE_CANCELLED = "cancel_email_queue";
    public static final String ROUTING_KEY_CANCELLED = "booking.cancelled";

    // Rescheduled (New Feature Added)
    public static final String QUEUE_RESCHEDULED = "reschedule_email_queue";
    public static final String ROUTING_KEY_RESCHEDULED = "booking.rescheduled";

    // Audit Logging Queue & Routing Key (NEW ADDITION)
    public static final String QUEUE_AUDIT = "audit_log_queue";
    public static final String ROUTING_KEY_AUDIT = "audit.log";


    // confiremd
    @Bean
    public Queue confirmedQueue() {
        return new Queue(QUEUE_CONFIRMED, true); // durable 🔥
    }

    // cancelled
    @Bean
    public Queue cancelledQueue(){
        return new Queue(QUEUE_CANCELLED,true);
    }

    @Bean
    public Queue rescheduledQueue(){
        return new Queue(QUEUE_RESCHEDULED,true);
    }

    @Bean
    public Queue auditLogQueue(){
        return new Queue(QUEUE_AUDIT,true);
    }

    // same for both
    @Bean
    public TopicExchange exchange (){
        return new TopicExchange(EXCHANGE);
    }

    // confirmed
    @Bean
    public Binding binding(Queue confirmedQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(confirmedQueue)
                .to(exchange)
                .with(ROUTING_KEY_CONFIRMED);
    }

    // for Canceled
    @Bean
    public Binding cancelbinding(Queue cancelledQueue,TopicExchange exchange){
        return BindingBuilder
                .bind(cancelledQueue)
                .to(exchange)
                .with(ROUTING_KEY_CANCELLED);
    }

    @Bean
    public Binding bindingRescheduled(Queue  rescheduledQueue,TopicExchange exchange){
        return BindingBuilder
                .bind(rescheduledQueue)
                .to(exchange)
                .with(ROUTING_KEY_RESCHEDULED);
    }

    @Bean
    public Binding bindingAuditLog(Queue auditLogQueue,TopicExchange exchange){
        return BindingBuilder
                .bind(auditLogQueue)
                .to(exchange)
                .with(ROUTING_KEY_AUDIT);
    }

    // JSON Converter for clean RabbitMQ payloads
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
}
