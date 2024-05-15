package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangesConfig {

    // create reservation
    @Bean
    public FanoutExchange createRoomReservationRequestExchange() {
        return new FanoutExchange("hotels.requests.createReservation");
    }

    @Bean
    public Queue createRoomReservationRequestQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding createHotelReservationRequestBinding(FanoutExchange createRoomReservationRequestExchange, Queue createRoomReservationRequestQueue) {
        return BindingBuilder.bind(createRoomReservationRequestQueue).to(createRoomReservationRequestExchange);
    }
}
