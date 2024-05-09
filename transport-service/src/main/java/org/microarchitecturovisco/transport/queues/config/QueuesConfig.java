package org.microarchitecturovisco.transport.queues.config;

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
        return new Queue("transports.response.getTransportsBySearchQuery", false);
    }
}
