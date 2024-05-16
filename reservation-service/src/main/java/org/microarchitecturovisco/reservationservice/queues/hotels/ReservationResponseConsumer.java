package org.microarchitecturovisco.reservationservice.queues.hotels;

import org.microarchitecturovisco.reservationservice.queues.config.QueuesConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationResponseConsumer {

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK_RES)
    public void consumeResponseMessageFromQueue(ReservationResponse response) {
        System.out.println("Response received from queue: " + response);

        // todo:
        //  Process the response - check if the hotel is available or not

    }
}
