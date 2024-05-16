package org.microarchitecturovisco.transport.queues.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_BOOK_REQ = "transports.requests.checkAvailabilityByQuery.queue";

    @Bean
    public Queue handleReservationQueue() {
        return new Queue(QUEUE_TRANSPORT_BOOK_REQ);
    }

    @Bean
    public TopicExchange handleReservationExchange() {
        return new TopicExchange(EXCHANGE_TRANSPORT);
    }

    @Bean
    public Binding handleReservationRequestBinding(TopicExchange handleReservationExchange, Queue handleReservationQueue) {
        return BindingBuilder.bind(handleReservationQueue).to(handleReservationExchange).with(QUEUE_TRANSPORT_BOOK_REQ);
    }
    
    @Bean
    public Queue getTransportsRequest() {
        return new Queue("transports.requests.getTransportsBySearchQuery", false);
    }

    @Bean
    public Queue getTransportsResponse() {
        return new Queue("transports.responses.getTransportsBySearchQuery", false);
    }
}
