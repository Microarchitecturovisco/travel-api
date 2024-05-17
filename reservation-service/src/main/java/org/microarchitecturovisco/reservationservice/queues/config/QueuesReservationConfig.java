package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesReservationConfig {

    public static final String EXCHANGE_RESERVATION = "reservations.events.createReservation.exchange";
    public static final String QUEUE_RESERVATION_CREATE_REQ = "reservations.events.createReservation.queue";
    public static final String ROUTING_KEY_RESERVATION_CREATE_REQ = "reservations.events.createReservation.routingKey";


    @Bean(name="handleReservationCreateExchange")
    public TopicExchange handleReservationCreateExchange() {
        return new TopicExchange(EXCHANGE_RESERVATION);
    }
    @Bean
    public Queue handleReservationCreateQueue() {
        return new Queue(QUEUE_RESERVATION_CREATE_REQ);
    }
    @Bean
    public Binding handleReservationCreateRequestBinding(@Qualifier("handleReservationCreateExchange") TopicExchange handleReservationCreateExchange, Queue handleReservationCreateQueue) {
        return BindingBuilder.bind(handleReservationCreateQueue).to(handleReservationCreateExchange).with(ROUTING_KEY_RESERVATION_CREATE_REQ);
    }

}
