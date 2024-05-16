package org.microarchitecturovisco.hotelservice.queues.config;

import org.microarchitecturovisco.hotelservice.reservations.ReservationConsumer;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL = "hotels.requests.checkAvailabilityByQuery.exchange";
    public static final String QUEUE_HOTEL_BOOK = "hotels.requests.checkAvailabilityByQuery.queue";
    public static final String ROUTING_KEY_HOTEL_BOOK = "hotels.requests.checkAvailabilityByQuery.routingKey";



    @Bean
    public Queue getHotelsRequest() {
        return new Queue("hotels.requests.getHotelsBySearchQuery", false);
    }

    @Bean
    public Queue getHotelsResponse() {
        return new Queue("hotels.responses.getHotelsBySearchQuery", false);
    }
}
