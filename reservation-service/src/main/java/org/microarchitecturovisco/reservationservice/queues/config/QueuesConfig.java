package org.microarchitecturovisco.reservationservice.queues.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    public static final String EXCHANGE_TRANSPORT = "transports.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_TRANSPORT_CHECK_AVAILABILITY_REQ = "transports.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_TRANSPORT_CHECK_AVAILABILITY_REQ = "transports.requests.checkAvailabilityByQuery.routingKey";

}
