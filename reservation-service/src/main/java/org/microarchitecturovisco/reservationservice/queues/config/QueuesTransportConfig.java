package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesTransportConfig {

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.routingKey";

    public static final String EXCHANGE_TRANSPORT_FANOUT = "transports.createReservation.exchange";

    @Bean(name="fanoutExchangeTransport")
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT);
    }


    @Bean(name="handleTransportReservationExchange")
    public TopicExchange handleTransportReservationExchange() {
        return new TopicExchange(EXCHANGE_TRANSPORT);
    }

    @Bean
    public Queue handleTransportReservationQueue() {
        return new Queue(QUEUE_TRANSPORT_BOOK_REQ, false, false, true);
    }
    
    @Bean
    public Binding handleTransportReservationRequestBinding(@Qualifier("handleTransportReservationExchange") TopicExchange handleTransportReservationExchange, Queue handleTransportReservationQueue) {
        return BindingBuilder.bind(handleTransportReservationQueue).to(handleTransportReservationExchange).with(ROUTING_KEY_TRANSPORT_BOOK_REQ);
    }

    public static final String EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION = "transports.deleteReservation.exchange";

    @Bean(name="fanoutExchangeTransportDeleteReservation")
    public FanoutExchange fanoutExchangeTransportDeleteReservation() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION);
    }
}
