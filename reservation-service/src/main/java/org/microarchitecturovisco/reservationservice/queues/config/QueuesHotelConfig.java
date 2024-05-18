package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesHotelConfig {

    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.routingKey";

    public static final String QUEUE_HOTEL_CREATE_RESERVATION_REQ = "hotels.events.createHotelReservation.queue";
    public static final String ROUTING_KEY_HOTEL_CREATE_RESERVATION_REQ = "hotels.events.createHotelReservation.routingKey";

    @Bean
    @Qualifier("handleHotelExchange")
    public TopicExchange handleHotelExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }

    @Bean
    @Qualifier("handleReservationQueue")
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ, false);
    }

    @Bean
    public Binding handleReservationRequestBinding(@Qualifier("handleHotelExchange") TopicExchange exchange,
                                                   @Qualifier("handleReservationQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_HOTEL_CHECK_AVAILABILITY_REQ);
    }

    @Bean
    @Qualifier("handleCreateHotelReservationQueue")
    public Queue handleCreateHotelReservationQueue() {
        return new Queue(QUEUE_HOTEL_CREATE_RESERVATION_REQ, false);
    }

    @Bean
    public Binding handleCreateHotelReservationRequestBinding(@Qualifier("handleHotelExchange") TopicExchange exchange,
                                                              @Qualifier("handleCreateHotelReservationQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_HOTEL_CREATE_RESERVATION_REQ);
    }
}
