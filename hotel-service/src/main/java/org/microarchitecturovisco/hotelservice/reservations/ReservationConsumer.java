package org.microarchitecturovisco.hotelservice.reservations;

import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationConsumer {

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK)
    public void consumeMessageFromQueue(ReservationRequest request) {
        System.out.println("Message received from queue - example: " + request);
        System.out.println("Message received from queue - example: " + request.getRoomIds());
    }
}