package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue getHotelsRequest() {
        return new Queue("hotels.requests.getHotelsBySearchQuery", false);
    }

    @Bean
    public Queue getHotelsResponse() {
        return new Queue("hotels.responses.getHotelsBySearchQuery", false);
    }
}
