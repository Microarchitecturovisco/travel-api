package org.microarchitecturovisco.offerprovider.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue getTransportsRequest() {
        return new Queue("transports.requests.getTransportsBySearchQuery", false);
    }

    @Bean
    public Queue getTransportsResponse() {
        return new Queue("transports.responses.getTransportsBySearchQuery", false);
    }

    @Bean(name = "getTransportsExchange")
    public DirectExchange getTransportsExchange() {
        return new DirectExchange("transports.requests.getTransportsBySearchQuery");
    }

    @Bean(name = "getHotelsExchange")
    public DirectExchange getHotelsExchange() {
        return new DirectExchange("hotels.requests.hotelsBySearchQuery");
    }

    public static final String EXCHANGE_DATA_GENERATOR_HOTELS = "data.generate.hotels.exchange";

    @Bean
    public FanoutExchange hotelDataGeneratorExchange() {
        return new FanoutExchange(EXCHANGE_DATA_GENERATOR_HOTELS);
    }

    @Bean
    public Queue hotelDataGeneratorQueue() {
        String uniqueQueueName = "data.generate.hotels.queue" + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, false);
    }

    @Bean
    public Binding hotelDataGeneratorBinding(
            FanoutExchange hotelDataGeneratorExchange,
            Queue hotelDataGeneratorQueue) {
        return BindingBuilder.bind(hotelDataGeneratorQueue).to(hotelDataGeneratorExchange);
    }

    public static final String EXCHANGE_DATA_GENERATOR_TRANSPORTS = "data.generate.transports.exchange";

    @Bean
    public FanoutExchange transportDataGeneratorExchange() {
        return new FanoutExchange(EXCHANGE_DATA_GENERATOR_TRANSPORTS);
    }

    @Bean
    public Queue transportDataGeneratorQueue() {
        String uniqueQueueName = "data.generate.transports.queue" + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, false);
    }

    @Bean
    public Binding transportDataGeneratorBinding(
            FanoutExchange transportDataGeneratorExchange,
            Queue transportDataGeneratorQueue) {
        return BindingBuilder.bind(transportDataGeneratorQueue).to(transportDataGeneratorExchange);
    }
}
