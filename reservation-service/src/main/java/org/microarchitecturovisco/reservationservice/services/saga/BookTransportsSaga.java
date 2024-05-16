package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesTransportConfig;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTransportsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfTransportIsAvailable(ReservationRequest reservationRequest) {
        String result = (String) rabbitTemplate.convertSendAndReceive(
                QueuesTransportConfig.EXCHANGE_TRANSPORT,
                QueuesTransportConfig.ROUTING_KEY_TRANSPORT_BOOK_REQ,
                reservationRequest
        );

        return Boolean.parseBoolean(result);
    }

}
