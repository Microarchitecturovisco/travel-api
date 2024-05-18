package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesTransportConfig {

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.routingKey";

    public static final String EXCHANGE_TRANSPORT_FANOUT = "transports.createReservation.exchange";


    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT);
    }
}
