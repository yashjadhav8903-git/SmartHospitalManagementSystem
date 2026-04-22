package com.example.HospitalManagement.Rabbit_MQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.springframework.amqp.core.Queue;

@Component
public class RabbitMQConfig {

    // Confirmend message properties
    public static final String QUEUE = "email_queue";
    public static final String EXCHANGE = "booking_exchange";
    public static final String ROUTING_KEY = "booking.confirmed";

    // Cancelled message properties ( same exchange )
    public static final String QUEUE1 = "cancel_email_queue";
    public static final String ROUTING_KEY1 = "booking.cancelled ";

    // confiremd
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); // durable 🔥
    }

    // cancelled
    @Bean
    public Queue queue1(){
        return new Queue(QUEUE1,true);
    }

    // same for both
    @Bean
    public TopicExchange exchange (){
        return new TopicExchange(EXCHANGE);
    }

    // confiremd
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    // for Cnacelled
    @Bean
    public Binding cancelbinding(Queue queue1,TopicExchange exchange){
        return BindingBuilder
                .bind(queue1)
                .to(exchange)
                .with(ROUTING_KEY1);
    }
}
