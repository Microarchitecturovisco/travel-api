package org.microarchitecturovisco.reservationservice.queues.hotels;

import org.microarchitecturovisco.reservationservice.queues.config.QueuesConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReservationResponseConsumer {

    private final String HOTEL_AVAILABLE_MESSAGE = "HOTEL AVAILABLE";
    private final String HOTEL_NOT_AVAILABLE_MESSAGE = "HOTEL NOT AVAILABLE";

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK_RES)
    public void consumeResponseMessageFromQueue(ReservationResponse response) {
        System.out.println("Response received from queue: " + response);

        // todo: Process the response - check if the hotel is available or not

        String responseMessage = response.getMessage();
        if(Objects.equals(responseMessage, HOTEL_AVAILABLE_MESSAGE)){
            bookTransports(response.getReservationRequest());
        }
        else { // Rollback
            stopBookingProcess(response.getReservationRequest());
        }
    }


    private void bookTransports(ReservationRequest reservationRequest){
        // todo: continue the booking process, and reserve transport
    }
    private void stopBookingProcess(ReservationRequest reservationRequest){
        // todo: do ROLLBACK --> stop the booking process (dont reserve transport)

    }
}
