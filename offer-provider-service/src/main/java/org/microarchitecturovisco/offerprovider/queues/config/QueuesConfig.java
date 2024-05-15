package org.microarchitecturovisco.offerprovider.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return new DirectExchange("hotels.requests.getHotelsBySearchQuery");
    }


}
