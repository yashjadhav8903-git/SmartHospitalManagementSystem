package com.example.HospitalManagement.Rabbit_MQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.springframework.amqp.core.Queue;

@Component
public class RabbitMQConfig {

    public static final String QUEUE = "email_queue";
    public static final String EXCHANGE = "booking_exchange";
    public static final String ROUTING_KEY = "booking.confirmed";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true); // durable 🔥
    }

    @Bean
    public TopicExchange exchange (){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
}
