package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CreateHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelDetailsRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelDetailsResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.hotelservice.services.HotelsCommandService;
import org.microarchitecturovisco.hotelservice.services.HotelsService;
import org.microarchitecturovisco.hotelservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.microarchitecturovisco.hotelservice.utils.JsonConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController()
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelsService hotelsService;
    private final HotelsCommandService hotelsCommandService;

    @RabbitListener(queues = "hotels.requests.hotelsBySearchQuery")
    public String consumeGetHotelsRequest(String requestDtoJson) {

        Logger logger = Logger.getLogger("getHotelsBySearchQuery");
        logger.info("Request: " + requestDtoJson);

        GetHotelsBySearchQueryRequestDto requestDto = JsonReader.readGetHotelsBySearchQueryRequestFromJson(requestDtoJson);
        GetHotelsBySearchQueryResponseDto responseDto = hotelsService.GetHotelsBySearchQuery(requestDto);

        logger.info("Response hotels size: " + responseDto.getHotels().size());

        return JsonConverter.convertGetHotelsBySearchQueryResponseDto(responseDto);
    }

    @RabbitListener(queues = "hotels.requests.getHotelDetails")
    public String consumeGetHotelDetails(String requestDtoJson) {

        Logger logger = Logger.getLogger("getHotelDetails");

        GetHotelDetailsRequestDto requestDto = JsonReader.readGetHotelDetailsRequestFromJson(requestDtoJson);
        GetHotelDetailsResponseDto responseDto = hotelsService.getHotelDetails(requestDto);

        logger.info("Response for hotel: " + responseDto.getHotelId() + " " + responseDto.getHotelName());

        return JsonConverter.convertGetHotelDetailsResponseDto(responseDto);
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_CHECK_AVAILABILITY_REQ)
    public String consumeMessageCheckHotelAvailability(String request) {

        Logger logger = Logger.getLogger("checkHotelAvailability");
        logger.info("Request: " + request);

        CheckHotelAvailabilityRequest availabilityRequest = JsonReader.readDtoFromJson(request, CheckHotelAvailabilityRequest.class);

        GetHotelsBySearchQueryRequestDto query = GetHotelsBySearchQueryRequestDto.builder()
                .dateFrom(availabilityRequest.getHotelTimeFrom())
                .dateTo(availabilityRequest.getHotelTimeTo())
                .arrivalLocationIds(availabilityRequest.getArrivalLocationIds())
                .adults(availabilityRequest.getAdultsQuantity())
                .childrenUnderThree(availabilityRequest.getChildrenUnder3Quantity())
                .childrenUnderTen(availabilityRequest.getChildrenUnder10Quantity())
                .childrenUnderEighteen(availabilityRequest.getChildrenUnder18Quantity())
                .build();

        GetHotelsBySearchQueryResponseDto hotels = hotelsService.GetHotelsBySearchQuery(query);

        logger.info("Response size: " + hotels.getHotels().size());

        return Boolean.toString(!hotels.getHotels().isEmpty());
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_CREATE_RESERVATION_REQ)
    public void consumeMessageCreateHotelReservation(CreateHotelReservationRequest request) {
        System.out.println("Message received from queue: " + request);

        int numberOfRoomsInReservation = request.getRoomReservationsIds().size();

        List<RoomReservationDto> roomReservations = new ArrayList<>();

        for (int i = 0; i < numberOfRoomsInReservation; i++) {
            UUID roomId = request.getRoomReservationsIds().get(i);

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
        }
    }
}

