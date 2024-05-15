package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangesConfig {

    // create reservation
    @Bean
    public FanoutExchange createHotelReservationRequestExchange() {
        return new FanoutExchange("hotels.requests.createReservation");
    }

    @Bean
    public Queue createHotelReservationRequestQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding createHotelReservationRequestBinding(FanoutExchange createHotelReservationRequestExchange, Queue createHotelReservationRequestQueue) {
        return BindingBuilder.bind(createHotelReservationRequestQueue).to(createHotelReservationRequestExchange);
    }
}
