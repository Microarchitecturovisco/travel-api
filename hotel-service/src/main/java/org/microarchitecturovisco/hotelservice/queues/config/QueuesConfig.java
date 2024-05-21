package org.microarchitecturovisco.hotelservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.queue";


    @Bean(name="handleReservationExchange")
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }

    @Bean(name="handleReservationQueue")
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ, false);
    }

    @Bean
    public Binding handleReservationRequestBinding(
            @Qualifier("handleReservationExchange") TopicExchange handleReservationExchange,
            @Qualifier("handleReservationQueue") Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ);
    }

    public static final String QUEUE_HOTEL_CREATE_RESERVATION_REQ_PREFIX = "hotels.events.createHotelReservation.queue.";
    public static final String EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION = "hotels.createReservation.exchange";

    @Bean(name="fanoutExchangeCreateHotelReservation")
    public FanoutExchange fanoutExchangeCreateHotelReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION);
    }

    @Bean(name="handleCreateHotelReservationQueue")
    public Queue handleCreateHotelReservationQueue() {
        String uniqueQueueName = QUEUE_HOTEL_CREATE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false);
    }

    @Bean
    public Binding handleCreateHotelReservationBinding(
            @Qualifier("fanoutExchangeCreateHotelReservation") FanoutExchange fanoutExchangeCreateHotelReservation,
            @Qualifier("handleCreateHotelReservationQueue") Queue handleCreateHotelReservationQueue) {
        return BindingBuilder.bind(handleCreateHotelReservationQueue).to(fanoutExchangeCreateHotelReservation);
    }


    public static final String QUEUE_HOTEL_DELETE_RESERVATION_REQ_PREFIX = "hotels.events.deleteHotelReservation.queue.";
    public static final String EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION = "hotels.deleteReservation.exchange";

    @Bean(name="fanoutExchangeDeleteHotelReservation")
    public FanoutExchange fanoutExchangeDeleteHotelReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION);
    }

    @Bean(name="handleDeleteHotelReservationQueue")
    public Queue handleDeleteHotelReservationQueue() {
        String uniqueQueueName = QUEUE_HOTEL_DELETE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false);
    }

    @Bean
    public Binding handleDeleteHotelReservationBinding(
            @Qualifier("fanoutExchangeDeleteHotelReservation") FanoutExchange fanoutExchangeDeleteHotelReservation,
            @Qualifier("handleDeleteHotelReservationQueue") Queue handleDeleteHotelReservationQueue) {
        return BindingBuilder.bind(handleDeleteHotelReservationQueue).to(fanoutExchangeDeleteHotelReservation);
    }
}
