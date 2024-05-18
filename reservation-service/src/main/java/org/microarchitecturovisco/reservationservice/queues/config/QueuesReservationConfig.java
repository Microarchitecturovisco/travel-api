package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.UUID;

@Configuration
public class QueuesReservationConfig {

    public static final String EXCHANGE_RESERVATION = "reservations.events.createReservation.exchange";

    @Bean
    public FanoutExchange handleReservationCreateExchange() {
        return new FanoutExchange(EXCHANGE_RESERVATION);
    }

    @Bean
    public Queue handleReservationCreateQueue() {
        String uniqueQueueName = "reservations.events.createReservation.queue." + UUID.randomUUID();
        return new Queue(uniqueQueueName, true);
    }

    @Bean
    public Binding handleReservationCreateRequestBinding(
            FanoutExchange handleReservationCreateExchange,
            Queue handleReservationCreateQueue) {
        return BindingBuilder.bind(handleReservationCreateQueue).to(handleReservationCreateExchange);
    }
}
