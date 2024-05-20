package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.config.*;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTransportsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfTransportIsAvailable(ReservationRequest reservationRequest) {
        CheckTransportAvailabilityRequest availabilityRequest = CheckTransportAvailabilityRequest.builder()
                .dateFrom(reservationRequest.getHotelTimeFrom())
                .dateTo(reservationRequest.getHotelTimeTo())
                .adults(reservationRequest.getAdultsQuantity())
                .childrenUnderThree(reservationRequest.getChildrenUnder3Quantity())
                .childrenUnderTen(reservationRequest.getChildrenUnder10Quantity())
                .childrenUnderEighteen(reservationRequest.getChildrenUnder18Quantity())
                .departureLocationIds(reservationRequest.getDepartureLocationIdsByPlane())
                .arrivalLocationIds(reservationRequest.getArrivalLocationIds())
                .transportType(TransportType.BUS) // todo change - get it from request
                .build();

        System.out.println("Sending data to transports: " + JsonConverter.convert(availabilityRequest));

        String result = (String) rabbitTemplate.convertSendAndReceive(
                QueuesTransportConfig.EXCHANGE_TRANSPORT,
                QueuesTransportConfig.ROUTING_KEY_TRANSPORT_CHECK_AVAILABILITY_REQ,
                JsonConverter.convert(availabilityRequest)
        );

        return Boolean.parseBoolean(result);
    }

    public void createTransportReservation(ReservationRequest reservationRequest) {
        rabbitTemplate.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT,
                "", // Routing key is ignored for FanoutExchange
                reservationRequest
        );
    }

    public void deleteTransportReservation(TransportReservationDeleteRequest transportReservationDeleteRequest) {
        rabbitTemplate.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                transportReservationDeleteRequest
        );
    }
}
