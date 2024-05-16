package org.microarchitecturovisco.transport.queues.reservations;

import org.microarchitecturovisco.transport.queues.config.QueuesConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationRequestConsumer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ReservationRequestConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_TRANSPORT_BOOK_REQ)
    public String consumeMessageFromQueue(ReservationRequest request) {
        System.out.println("Message received from queue - example: " + request);

        // todo:
        //  Here check if the transport specified in the request is available
        //  use transport module logic here


        // Prepare the response - transports available or not available
        boolean response = true;

        // Send the response message to the reservation service
        return Boolean.toString(response);
    }

}
