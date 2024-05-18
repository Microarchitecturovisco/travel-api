package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.queue";


    @Bean
    @Qualifier("handleReservationExchange")
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }

    @Bean
    @Qualifier("handleReservationQueue")
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ, false);
    }

    @Bean
    public Binding handleReservationRequestBinding(
            @Qualifier("handleReservationExchange") TopicExchange handleReservationExchange,
            @Qualifier("handleReservationQueue") Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ);
    }

    public static final String QUEUE_HOTEL_CREATE_RESERVATION_REQ = "hotels.events.createHotelReservation.queue";
    public static final String EXCHANGE_HOTEL_FANOUT = "hotels.createReservation.exchange";

    @Bean
    @Qualifier("fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT);
    }

    @Bean
    @Qualifier("handleCreateHotelReservationQueue")
    public Queue handleCreateHotelReservationQueue() {
        return new Queue(QUEUE_HOTEL_CREATE_RESERVATION_REQ, false);
    }

    @Bean
    public Binding handleCreateHotelReservationBinding(
            @Qualifier("fanoutExchange") FanoutExchange fanoutExchange,
            @Qualifier("handleCreateHotelReservationQueue") Queue handleCreateHotelReservationQueue) {
        return BindingBuilder.bind(handleCreateHotelReservationQueue).to(fanoutExchange);
    }
}
