package org.microarchitecturovisco.transport.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue getTransportsResponse() {
        return new Queue("transports.responses.getTransportsBySearchQuery", false);
    }

    @Bean
    public Queue handleTransportsBySearchQuery() {
        return new Queue("transports.requests.getTransportsBySearchQuery", false);
    }

    @Bean
    public DirectExchange handleTransportsBySearchQueryExchange() {
        return new DirectExchange("transports.requests.getTransportsBySearchQuery");
    }

    @Bean
    public Binding handleTransportsBySearchQueryRequestBinding(DirectExchange handleTransportsBySearchQueryExchange, Queue handleTransportsBySearchQuery) {
        return BindingBuilder.bind(handleTransportsBySearchQuery).to(handleTransportsBySearchQueryExchange).with("transports.handleTransportsBySearchQuery");
    }
}
