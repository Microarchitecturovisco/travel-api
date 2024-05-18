package org.microarchitecturovisco.transport.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue getTransportsResponse() {
        return new Queue("transports.responses.getTransportsBySearchQuery", false);
    }

    @Bean
    public Queue handleTransportsBySearchQuery() {
        return new Queue("transports.requests.getTransportsBySearchQuery", false);
    }

    @Bean
    public DirectExchange handleTransportsBySearchQueryExchange() {
        return new DirectExchange("transports.requests.getTransportsBySearchQuery");
    }

    @Bean
    public Binding handleTransportsBySearchQueryRequestBinding(DirectExchange handleTransportsBySearchQueryExchange, Queue handleTransportsBySearchQuery) {
        return BindingBuilder.bind(handleTransportsBySearchQuery).to(handleTransportsBySearchQueryExchange).with("transports.handleTransportsBySearchQuery");
    }


    public static final String QUEUE_TRANSPORT_CREATE_RESERVATION_REQ = "transports.events.createTransportReservation.queue";
    public static final String EXCHANGE_TRANSPORT_FANOUT = "transports.createReservation.exchange";

    @Bean
    @Qualifier("fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT);
    }

    @Bean
    @Qualifier("handleCreateTransportReservationQueue")
    public Queue handleCreateTransportReservationQueue() {
        return new Queue(QUEUE_TRANSPORT_CREATE_RESERVATION_REQ, false);
    }

    public Binding handleCreateTransportReservationBinding(
            @Qualifier("fanoutExchange") FanoutExchange fanoutExchange,
            @Qualifier("handleCreateTransportReservationQueue") Queue handleCreateTransportReservationQueue) {
        return BindingBuilder.bind(handleCreateTransportReservationQueue).to(fanoutExchange);
    }

}
