package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.CreateHotelReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.responses.CheckHotelAvailabilityResponseDto;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.HotelReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesHotelConfig;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.microarchitecturovisco.reservationservice.utils.json.JsonReader;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookHotelsSaga {
    private final RabbitTemplate rabbitTemplate;

    public boolean checkIfHotelIsAvailable(ReservationRequest reservationRequest) throws ReservationFailException {
        CheckHotelAvailabilityRequest availabilityRequest = CheckHotelAvailabilityRequest.builder()
                .dateFrom(reservationRequest.getHotelTimeFrom())
                .dateTo(reservationRequest.getHotelTimeTo())
                .roomReservationsIds(reservationRequest.getRoomReservationsIds())
                .hotelId(reservationRequest.getHotelId())
                .build();

        String reservationRequestJson = JsonConverter.convert(availabilityRequest);

        System.out.println("Checking hotel availability: " + availabilityRequest);

        try {
            byte[] responseMessageB = (byte[]) rabbitTemplate.convertSendAndReceive(
                    QueuesHotelConfig.EXCHANGE_HOTEL,
                    QueuesHotelConfig.ROUTING_KEY_HOTEL_CHECK_AVAILABILITY_REQ,
                    reservationRequestJson);

            if(responseMessageB != null) {
                String responseMessage = (new String(responseMessageB)).replace("\\", "");
                responseMessage = responseMessage.substring(1, responseMessage.length() - 1);
                CheckHotelAvailabilityResponseDto response = JsonReader.readDtoFromJson(responseMessage, CheckHotelAvailabilityResponseDto.class);
                return response.isIfAvailable();
            }
            else {
                System.out.println("Null message at: checkIfHotelIsAvailable()");
                throw new ReservationFailException();
            }
        } catch (ReservationFailException e) {
            e.printStackTrace();
            throw new ReservationFailException();
        }
    }
    public void createHotelReservation(ReservationRequest reservationRequest) {
        CreateHotelReservationRequest request = CreateHotelReservationRequest.builder()
                                            .hotelTimeFrom(reservationRequest.getHotelTimeFrom())
                                            .hotelTimeTo(reservationRequest.getHotelTimeTo())
                                            .hotelId(reservationRequest.getHotelId())
                                            .reservationId(reservationRequest.getId())
                                            .roomIds(reservationRequest.getRoomReservationsIds())
                                            .build();

        String requestJson = JsonConverter.convert(request);

        System.out.println("Creating Hotel reservation: " + requestJson);

        rabbitTemplate.convertAndSend(
                QueuesHotelConfig.EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                requestJson
        );
    }

    public void deleteHotelReservation(HotelReservationDeleteRequest hotelReservationDeleteRequest) {
        String requestJson = JsonConverter.convert(hotelReservationDeleteRequest);

        System.out.println("Deleting hotel reservation: " + requestJson);

        rabbitTemplate.convertAndSend(
                QueuesHotelConfig.EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                requestJson
        );
    }

}
