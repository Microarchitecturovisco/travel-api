package org.microarchitecturovisco.hotelservice.queues.reservations;

import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationRequestConsumer {

    private final String HOTEL_AVAILABLE_MESSAGE = "HOTEL AVAILABLE";
    private final String HOTEL_NOT_AVAILABLE_MESSAGE = "HOTEL NOT AVAILABLE";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ReservationRequestConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK_REQ)
    public void consumeMessageFromQueue(ReservationRequest request) {
        System.out.println("Message received from queue - example: " + request);

        // todo:
        //  Here check if the hotel specified in the request is available
        //  use hotel module logic here


        // Prepare the response - hotels available or not available
        ReservationResponse response = buildResponse(request, HOTEL_AVAILABLE_MESSAGE);
        // ReservationResponse response = buildResponse(request, HOTEL_NOT_AVAILABLE_MESSAGE);

        // Send the response message to the reservation service
        rabbitTemplate.convertAndSend(
                QueuesConfig.EXCHANGE_HOTEL,
                QueuesConfig.ROUTING_KEY_HOTEL_BOOK_RES,
                response);
    }

    private ReservationResponse buildResponse(ReservationRequest request, String responseMessage) {
        return new ReservationResponse(request, responseMessage);
    }
}
