package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesHotelConfig {

    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_CHECK_AVAILABILITY_REQ = "hotels.requests.checkAvailabilityByQuery.routingKey";

    @Bean(name="handleHotelExchange")
    public TopicExchange handleHotelExchange() {
        return new TopicExchange(EXCHANGE_HOTEL);
    }


    @Bean(name="handleReservationQueue")
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_HOTEL_CHECK_AVAILABILITY_REQ, false);
    }

    @Bean
    public Binding handleReservationRequestBinding(@Qualifier("handleHotelExchange") TopicExchange exchange,
                                                   @Qualifier("handleReservationQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_HOTEL_CHECK_AVAILABILITY_REQ);
    }


    public static final String EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION = "hotels.createReservation.exchange";

    @Bean(name="fanoutExchangeHotelCreateReservation")
    public FanoutExchange fanoutExchangeHotelCreateReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION);
    }


    public static final String EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION = "hotels.deleteReservation.exchange";

    @Bean(name="fanoutExchangeHotelDeleteReservation")
    public FanoutExchange fanoutExchangeHotelDeleteReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION);
    }

}
