package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QueuesReservationConfig {

    public static final String EXCHANGE_CREATE_RESERVATION = "reservations.events.createReservation.exchange";

    @Bean
    public FanoutExchange handleReservationCreateExchange() {
        return new FanoutExchange(EXCHANGE_CREATE_RESERVATION);
    }

    @Bean
    public Queue handleReservationCreateQueue() {
        String uniqueQueueName = "reservations.events.createReservation.queue." + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleReservationCreateRequestBinding(
            FanoutExchange handleReservationCreateExchange,
            Queue handleReservationCreateQueue) {
        return BindingBuilder.bind(handleReservationCreateQueue).to(handleReservationCreateExchange);
    }


    public static final String EXCHANGE_DELETE_RESERVATION = "reservations.events.deleteReservation.exchange";

    @Bean
    public FanoutExchange handleReservationDeleteExchange() {
        return new FanoutExchange(EXCHANGE_DELETE_RESERVATION);
    }

    @Bean
    public Queue handleReservationDeleteQueue() {
        String uniqueQueueName = "reservations.events.deleteReservation.queue." + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleReservationDeleteRequestBinding(
            FanoutExchange handleReservationDeleteExchange,
            Queue handleReservationDeleteQueue) {
        return BindingBuilder.bind(handleReservationDeleteQueue).to(handleReservationDeleteExchange);
    }
}
