package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_BOOK_REQ = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_BOOK_REQ = "hotels.requests.checkAvailabilityByQuery.routingKey";

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.routingKey";


    @Bean
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }


    @Bean
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_BOOK_REQ, false);
    }
    @Bean
    public Binding handleReservationRequestBinding(TopicExchange handleReservationExchange, Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(ROUTING_KEY_HOTEL_BOOK_REQ);
    }
}
