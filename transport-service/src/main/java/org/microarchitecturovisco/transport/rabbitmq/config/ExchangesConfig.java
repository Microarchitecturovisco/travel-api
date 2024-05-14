package org.microarchitecturovisco.transport.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangesConfig {

    // create reservation
    @Bean
    public FanoutExchange createTransportReservationRequestExchange() {
        return new FanoutExchange("transports.requests.createReservation");
    }

    @Bean
    public Queue createTransportReservationRequestQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding createTransportReservationRequestBinding(FanoutExchange createTransportReservationRequestExchange, Queue createTransportReservationRequestQueue) {
        return BindingBuilder.bind(createTransportReservationRequestQueue).to(createTransportReservationRequestExchange);
    }
}
