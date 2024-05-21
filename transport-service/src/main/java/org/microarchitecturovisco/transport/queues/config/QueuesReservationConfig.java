package org.microarchitecturovisco.transport.queues.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QueuesReservationConfig {

    public static final String QUEUE_TRANSPORT_CREATE_RESERVATION_REQ_PREFIX = "transports.createTransportReservation.queue.";
    public static final String EXCHANGE_TRANSPORT_FANOUT = "transports.createReservation.exchange";

    @Bean(name="fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT);
    }

    @Bean(name="handleCreateTransportReservationQueue")
    public Queue handleCreateTransportReservationQueue() {
        String uniqueQueueName = QUEUE_TRANSPORT_CREATE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleCreateTransportReservationBinding(
            @Qualifier("fanoutExchange") FanoutExchange fanoutExchange,
            @Qualifier("handleCreateTransportReservationQueue") Queue handleCreateTransportReservationQueue) {
        return BindingBuilder.bind(handleCreateTransportReservationQueue).to(fanoutExchange);
    }


    public static final String QUEUE_TRANSPORT_DELETE_RESERVATION_REQ_PREFIX = "transports.deleteTransportReservation.queue.";
    public static final String EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION = "transports.deleteReservation.exchange";

    @Bean(name="fanoutExchangeDeleteTransportReservation")
    public FanoutExchange fanoutExchangeDeleteTransportReservation() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION);
    }

    @Bean(name="handleDeleteTransportReservationQueue")
    public Queue handleDeleteTransportReservationQueue() {
        String uniqueQueueName = QUEUE_TRANSPORT_DELETE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleDeleteTransportReservationBinding(
            @Qualifier("fanoutExchangeDeleteTransportReservation") FanoutExchange fanoutExchangeDeleteTransportReservation,
            @Qualifier("handleDeleteTransportReservationQueue") Queue handleDeleteTransportReservationQueue) {
        return BindingBuilder.bind(handleDeleteTransportReservationQueue).to(fanoutExchangeDeleteTransportReservation);
    }
}
