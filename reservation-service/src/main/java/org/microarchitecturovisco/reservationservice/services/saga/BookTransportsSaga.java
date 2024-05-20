package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.config.*;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.CheckTransportAvailabilityRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.TransportReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTransportsSaga {
    private final RabbitTemplate rabbitTemplateObject;
    private final RabbitTemplate rabbitTemplateJSON;

    public boolean checkIfTransportIsAvailable(ReservationRequest reservationRequest) {
        CheckTransportAvailabilityRequest availabilityRequest = CheckTransportAvailabilityRequest.builder()
                .dateFrom(reservationRequest.getHotelTimeFrom())
                .dateTo(reservationRequest.getHotelTimeTo())
                .adults(reservationRequest.getAdultsQuantity())
                .childrenUnderThree(reservationRequest.getChildrenUnder3Quantity())
                .childrenUnderTen(reservationRequest.getChildrenUnder10Quantity())
                .childrenUnderEighteen(reservationRequest.getChildrenUnder18Quantity())
                .transportReservationsIdFrom(reservationRequest.getTransportReservationsIds().get(0))
                .transportReservationsIdArrival(reservationRequest.getTransportReservationsIds().get(1))
                .build();

        System.out.println("Sending data to transports: " + JsonConverter.convert(availabilityRequest));

        String result = (String) rabbitTemplateJSON.convertSendAndReceive(
                QueuesTransportConfig.EXCHANGE_TRANSPORT,
                QueuesTransportConfig.ROUTING_KEY_TRANSPORT_CHECK_AVAILABILITY_REQ,
                JsonConverter.convert(availabilityRequest)
        );

        return Boolean.parseBoolean(result);
    }

    public void createTransportReservation(ReservationRequest reservationRequest) {
        rabbitTemplateObject.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT,
                "", // Routing key is ignored for FanoutExchange
                reservationRequest
        );
    }

    public void deleteTransportReservation(TransportReservationDeleteRequest transportReservationDeleteRequest) {
        rabbitTemplateObject.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                transportReservationDeleteRequest
        );
    }
}
