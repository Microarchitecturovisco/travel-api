package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CreateHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelDetailsRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.CheckHotelAvailabilityResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelDetailsResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.hotelservice.services.HotelsCommandService;
import org.microarchitecturovisco.hotelservice.services.HotelsService;
import org.microarchitecturovisco.hotelservice.utils.JsonConverter;
import org.microarchitecturovisco.hotelservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelsService hotelsService;
    private final HotelsCommandService hotelsCommandService;

    @RabbitListener(queues = "hotels.requests.hotelsBySearchQuery")
    public String consumeGetHotelsRequest(String requestDtoJson) {

        GetHotelsBySearchQueryRequestDto requestDto = JsonReader.readGetHotelsBySearchQueryRequestFromJson(requestDtoJson);
        GetHotelsBySearchQueryResponseDto responseDto = hotelsService.GetHotelsBySearchQuery(requestDto);

        System.out.println("Send hotels response size " + responseDto.getHotels().size());


        return JsonConverter.convertGetHotelsBySearchQueryResponseDto(responseDto);
    }

    @RabbitListener(queues = "hotels.requests.getHotelDetails")
    public String consumeGetHotelDetails(String requestDtoJson) {

        GetHotelDetailsRequestDto requestDto = JsonReader.readGetHotelDetailsRequestFromJson(requestDtoJson);
        GetHotelDetailsResponseDto responseDto = hotelsService.getHotelDetails(requestDto);


        return JsonConverter.convertGetHotelDetailsResponseDto(responseDto);
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_CHECK_AVAILABILITY_REQ)
    public String consumeMessageCheckHotelAvailability(String requestJson) {
        System.out.println("Message received from queue: " + requestJson);
        CheckHotelAvailabilityRequest request = JsonReader.readCheckHotelAvailabilityRequestCommand(requestJson);
        System.out.println("Converted message received from queue: " + request);

        // todo: here logic (RSWW-102)

        CheckHotelAvailabilityResponseDto response = CheckHotelAvailabilityResponseDto.builder()
                        .ifAvailable(true) // todo adjust
                        .build();

        System.out.println("Response to convert:" +response );
        String responseJson = JsonConverter.ConvertToJson(response);
        System.out.println("Response after conversion:" +responseJson );

        return responseJson;
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_CREATE_RESERVATION_REQ)
    public void consumeMessageCreateHotelReservation(String requestJson) {
        System.out.println("Message received from queue: " + requestJson);

        CreateHotelReservationRequest request = JsonReader.readCreateHotelReservationRequestCommand(requestJson);

        int numberOfRoomsInReservation = request.getRoomIds().size();

        List<RoomReservationDto> roomReservations = new ArrayList<>();

        for (int i = 0; i < numberOfRoomsInReservation; i++) {
            UUID roomId = request.getRoomIds().get(i);

            RoomReservationDto roomReservation = new RoomReservationDto();
            roomReservation.setReservationId(request.getReservationId());
            roomReservation.setDateFrom(request.getHotelTimeFrom());
            roomReservation.setDateTo(request.getHotelTimeTo());
            roomReservation.setHotelId(request.getHotelId());
            roomReservation.setRoomId(roomId);

            roomReservations.add(roomReservation);
        }

        for (RoomReservationDto roomReservation : roomReservations){
            hotelsCommandService.createReservation(CreateRoomReservationCommand.builder()
                    .hotelId(roomReservation.getHotelId())
                    .roomId(roomReservation.getRoomId())
                    .roomReservationDto(roomReservation)
                    .commandTimeStamp(LocalDateTime.now())
                    .build()
            );
            System.out.println("roomReservation: " + roomReservation);
        }
    }
}

