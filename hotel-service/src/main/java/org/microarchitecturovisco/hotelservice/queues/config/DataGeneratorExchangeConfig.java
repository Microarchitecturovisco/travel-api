package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import java.util.UUID;

@Configuration
public class DataGeneratorExchangeConfig {

    public static final String EXCHANGE_DATA_GENERATOR = "data.generate.hotels.exchange";

    @Bean
    public FanoutExchange handleDataGeneratorExchange() {
        return new FanoutExchange(EXCHANGE_DATA_GENERATOR);
    }

    @Bean
    public Queue handleDataGeneratorCreateQueue() {
        String uniqueQueueName = "data.generate.hotels.queue" + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, false);
    }

    @Bean
    public Binding handleDataGeneratorBinding(
            FanoutExchange handleDataGeneratorExchange,
            Queue handleDataGeneratorCreateQueue) {
        return BindingBuilder.bind(handleDataGeneratorCreateQueue).to(handleDataGeneratorExchange);
    }
}
