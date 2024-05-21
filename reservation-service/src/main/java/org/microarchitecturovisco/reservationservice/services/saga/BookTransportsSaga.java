package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.CheckTransportAvailabilityRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.CreateTransportReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.TransportReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.responses.CheckTransportAvailabilityResponseDto;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesTransportConfig;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.microarchitecturovisco.reservationservice.utils.json.JsonReader;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTransportsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfTransportIsAvailable(ReservationRequest reservationRequest) throws ReservationFailException {
        int amountOfQuests = reservationRequest.getAdultsQuantity() + reservationRequest.getChildrenUnder18Quantity()
                + reservationRequest.getChildrenUnder10Quantity() + reservationRequest.getChildrenUnder3Quantity();

        CheckTransportAvailabilityRequest availabilityRequest = CheckTransportAvailabilityRequest.builder()
                .dateFrom(reservationRequest.getHotelTimeFrom())
                .dateTo(reservationRequest.getHotelTimeTo())
                .numberOfGuests(amountOfQuests)
                .transportReservationsIdFrom(reservationRequest.getTransportReservationsIds().get(0))
                .transportReservationsIdArrival(reservationRequest.getTransportReservationsIds().get(1))
                .build();

        String requestJson = JsonConverter.convert(availabilityRequest);

        System.out.println("Checking transport availability: " + availabilityRequest);

        String responseJson = (String) rabbitTemplate.convertSendAndReceive(
                    QueuesTransportConfig.EXCHANGE_TRANSPORT,
                    QueuesTransportConfig.ROUTING_KEY_TRANSPORT_CHECK_AVAILABILITY_REQ,
                    requestJson);

        CheckTransportAvailabilityResponseDto response = JsonReader.readDtoFromJson(responseJson, CheckTransportAvailabilityResponseDto.class);

        return response.isIfAvailable();
    }

    public void createTransportReservation(ReservationRequest reservationRequest) {
        int amountOfQuests = reservationRequest.getAdultsQuantity() + reservationRequest.getChildrenUnder18Quantity()
                + reservationRequest.getChildrenUnder10Quantity() + reservationRequest.getChildrenUnder3Quantity();

        CreateTransportReservationRequest request = CreateTransportReservationRequest.builder()
                .hotelTimeFrom(reservationRequest.getHotelTimeFrom())
                .hotelTimeTo(reservationRequest.getHotelTimeTo())
                .amountOfQuests(amountOfQuests)
                .transportIds(reservationRequest.getTransportReservationsIds())
                .reservationId(reservationRequest.getId())
                .build();

        String requestJson = JsonConverter.convert(request);

        System.out.println("Creating Transport reservation: " + requestJson);

        rabbitTemplate.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT,
                "", // Routing key is ignored for FanoutExchange
                requestJson
        );
    }

    public void deleteTransportReservation(TransportReservationDeleteRequest transportReservationDeleteRequest) {

        String requestJson = JsonConverter.convert(transportReservationDeleteRequest);
        System.out.println("Deleting transport reservation: " + requestJson);

        rabbitTemplate.convertAndSend(
                QueuesTransportConfig.EXCHANGE_TRANSPORT_FANOUT_DELETE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                requestJson
        );
    }
}
