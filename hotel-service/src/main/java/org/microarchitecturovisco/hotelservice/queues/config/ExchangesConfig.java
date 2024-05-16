package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangesConfig {


    @Bean
    public DirectExchange hotelsBySearchQuery() {
        return new DirectExchange("hotels.requests.hotelsBySearchQuery");
    }

    @Bean
    public Queue hotelsBySearchQueryQueue() { return new Queue("hotels.requests.hotelsBySearchQuery", false); }

    @Bean
    public Binding getHotelsBySearchQueryRequestBinding(DirectExchange hotelsBySearchQuery, Queue hotelsBySearchQueryQueue) {
        return BindingBuilder.bind(hotelsBySearchQueryQueue).to(hotelsBySearchQuery).with("hotels.requests.hotelsBySearchQuery");
    }

}
