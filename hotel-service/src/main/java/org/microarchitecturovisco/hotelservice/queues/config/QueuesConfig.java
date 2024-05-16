package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_BOOK_REQ = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String QUEUE_HOTEL_BOOK_RES = "hotels.responses.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_BOOK_RES = "hotels.responses.checkAvailabilityByQuery.routingKey";

    @Bean
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_BOOK_REQ);
    }

    @Bean
    public Queue handleReservationResponseQueue() {
        return new Queue(QUEUE_HOTEL_BOOK_RES);
    }

    @Bean
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }

    @Bean
    public Binding handleReservationRequestBinding(TopicExchange handleReservationExchange, Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(QUEUE_HOTEL_BOOK_REQ);
    }

    @Bean
    public Binding handleReservationResponseBinding(TopicExchange handleReservationExchange, Queue handleReservationResponseQueue) {
        return BindingBuilder.bind(handleReservationResponseQueue).to(handleReservationExchange).with(ROUTING_KEY_HOTEL_BOOK_RES);
    }
}
