package org.microarchitecturovisco.paymentservice.rabbitmq.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {
    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("org.microarchitecturovisco.*", "java.*"));
        return converter;
    }
}
