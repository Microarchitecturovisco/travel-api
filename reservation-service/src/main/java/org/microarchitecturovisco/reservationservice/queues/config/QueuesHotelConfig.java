package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesHotelConfig {

    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_BOOK_REQ = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_BOOK_REQ = "hotels.requests.checkAvailabilityByQuery.routingKey";

    @Bean(name="handleReservationExchange")
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }
    @Bean
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_BOOK_REQ);
    }
    @Bean
    public Binding handleReservationRequestBinding(@Qualifier("handleReservationExchange") TopicExchange handleReservationExchange, Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(ROUTING_KEY_HOTEL_BOOK_REQ);
    }
}
