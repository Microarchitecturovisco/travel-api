package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTransportsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfTransportIsAvailable(ReservationRequest reservationRequest) {
        String result = (String) rabbitTemplate.convertSendAndReceive(
                QueuesConfig.EXCHANGE_TRANSPORT,
                QueuesConfig.ROUTING_KEY_TRANSPORT_CHECK_AVAILABILITY_REQ,
                reservationRequest
        );

        return Boolean.parseBoolean(result);
    }

}
