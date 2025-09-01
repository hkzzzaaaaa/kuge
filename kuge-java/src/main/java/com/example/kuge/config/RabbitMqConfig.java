package com.example.kuge.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {
    public static final String DELAY_EXCHANGE_NAME = "order.delay.exchange";
    public static final String DELAY_QUEUE_NAME = "order.delay.queue";
    public static final String FAVOURITE_EXCHANGE = "music.favourite.exchange";
    public static final String FAVOURITE_ROUTING_KEY = "music.favourite.update";
    public static final String FAVOURITE_QUEUE = "music.favourite.queue";
    public static final String DELAY_ROUTING_KEY = "order.delay.routing";
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }
    @Bean
    public Queue delayQueue() {
        return new Queue(DELAY_QUEUE_NAME, true);
    }
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue())
                .to(delayExchange())
                .with(DELAY_ROUTING_KEY)
                .noargs();
    }
    @Bean
    public DirectExchange favouriteExchange() {
        return new DirectExchange(FAVOURITE_EXCHANGE, true, false);
    }

    @Bean
    public Queue favouriteQueue() {
        return new Queue(FAVOURITE_QUEUE, true, false, false);
    }
    @Bean
    public Binding favouriteBinding() {
        return BindingBuilder.bind(favouriteQueue())
                .to(favouriteExchange())
                .with(FAVOURITE_ROUTING_KEY);
    }
}
