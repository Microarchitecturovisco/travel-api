package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue handleReservationQueue() {
        return new Queue("reservations.requests.handle", false);
    }

    @Bean
    public DirectExchange handleReservationExchange() {
        return new DirectExchange("reservations.requests.handle");
    }

    @Bean
    public Binding handleReservationRequestBinding(DirectExchange handleReservationExchange, Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with("payments.handleReservation");
    }
}
