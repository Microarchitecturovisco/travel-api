package org.microarchitecturovisco.paymentservice.rabbitmq.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final CachingConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("org.microarchitecturovisco.*", "java.*"));
        return converter;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue handlePaymentTestQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public RabbitTemplate paymentRabbitTemplate(Jackson2JsonMessageConverter converter, ConnectionFactory connectionFactory, Queue handlePaymentTestQueue) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        rabbitTemplate.setReplyAddress(handlePaymentTestQueue.getName());
        return rabbitTemplate;
    }

//    @Bean
//    public RabbitTemplate amqpTemplate() {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(amqpTemplate().getConnectionFactory());
//        rabbitTemplate.setMessageConverter(converter());
//        rabbitTemplate.setReplyAddress(handlePaymentTestQueue().getName());
//        rabbitTemplate.setReplyTimeout(60000);
//        rabbitTemplate.setUseDirectReplyToContainer(false);
//        return rabbitTemplate;
//    }
}
