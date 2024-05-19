package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesTransportConfig {

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.routingKey";


    @Bean(name="handleTransportReservationExchange")
    public TopicExchange handleTransportReservationExchange() {
        return new TopicExchange(EXCHANGE_TRANSPORT);
    }
    @Bean
    public Queue handleTransportReservationQueue() {
        return new Queue(QUEUE_TRANSPORT_BOOK_REQ);
    }
    @Bean
    public Binding handleTransportReservationRequestBinding(@Qualifier("handleTransportReservationExchange") TopicExchange handleTransportReservationExchange, Queue handleTransportReservationQueue) {
        return BindingBuilder.bind(handleTransportReservationQueue).to(handleTransportReservationExchange).with(ROUTING_KEY_TRANSPORT_BOOK_REQ);
    }
}
