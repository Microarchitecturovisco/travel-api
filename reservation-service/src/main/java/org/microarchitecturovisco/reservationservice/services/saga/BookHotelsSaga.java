package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesHotelConfig;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookHotelsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfHotelIsAvailable(ReservationRequest reservationRequest) {
        String result = (String) rabbitTemplate.convertSendAndReceive(
                QueuesHotelConfig.EXCHANGE_HOTEL,
                QueuesHotelConfig.ROUTING_KEY_HOTEL_BOOK_REQ,
                reservationRequest
        );

        return Boolean.parseBoolean(result);
    }

}
