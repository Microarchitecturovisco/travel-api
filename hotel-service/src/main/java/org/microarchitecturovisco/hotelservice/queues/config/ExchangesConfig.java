package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangesConfig {

    // find available hotels by search query
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

    // return details of hotel with available room configurations
    @Bean
    public DirectExchange getHotelDetails() {
        return new DirectExchange("hotels.requests.getHotelDetails");
    }

    @Bean
    public Queue getHotelDetailsQueue() { return new Queue("hotels.requests.getHotelDetails", false); }

    @Bean
    public Binding getHotelDetailsRequestBinding(DirectExchange getHotelDetails, Queue getHotelDetailsQueue) {
        return BindingBuilder.bind(getHotelDetailsQueue).to(getHotelDetails).with("hotels.requests.getHotelDetails");
    }
}
