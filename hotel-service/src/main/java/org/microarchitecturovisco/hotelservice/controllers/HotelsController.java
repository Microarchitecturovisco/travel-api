package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CreateHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.DeleteHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.DeleteRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.microarchitecturovisco.hotelservice.model.dto.data_generator.RoomUpdateRequest;
import org.microarchitecturovisco.hotelservice.model.dto.request.CheckHotelAvailabilityQueryRequestDto;
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
    public String consumeMessageCheckHotelAvailability(String requestJson) {
        CheckHotelAvailabilityRequest request = JsonReader.readCheckHotelAvailabilityRequestCommand(requestJson);
        System.out.println("Checking hotel availability: " + request);

        CheckHotelAvailabilityQueryRequestDto query = CheckHotelAvailabilityQueryRequestDto.builder()
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .hotelId(request.getHotelId())
                .roomReservationsIds(request.getRoomReservationsIds())
                .build();

        boolean availability = hotelsService.CheckHotelAvailability(query);

        CheckHotelAvailabilityResponseDto response = CheckHotelAvailabilityResponseDto.builder()
                        .ifAvailable(availability)
                        .build();

        System.out.println("Hotel ifAvailable:" + response.isIfAvailable());
        String responseJson = JsonConverter.ConvertToJson(response);

        return responseJson;
    }

    @RabbitListener(queues = "#{handleCreateHotelReservationQueue.name}")
    public void consumeMessageCreateHotelReservation(String requestJson) {
        CreateHotelReservationRequest request = JsonReader.readCreateHotelReservationRequestCommand(requestJson);
        System.out.println("Creating hotel reservations: " + request);

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
        }
    }

    @RabbitListener(queues = "#{handleDeleteHotelReservationQueue.name}")
    public void consumeMessageDeleteHotelReservation(String requestJson) {

        DeleteHotelReservationRequest request = JsonReader.readDeleteHotelReservationRequestCommand(requestJson);
        System.out.println("Deleting hotel reservations: " + request);

        for (UUID roomId : request.getRoomIds()){
            DeleteRoomReservationCommand command = DeleteRoomReservationCommand.builder()
                    .commandTimeStamp(LocalDateTime.now())
                    .reservationId(request.getReservationId())
                    .roomId(roomId)
                    .hotelId(request.getHotelId())
                    .build();

            hotelsCommandService.deleteReservation(command);
        }
    }

    @RabbitListener(queues = "#{handleDataGeneratorCreateQueue}")
    public void consumeDataGeneratorMessage(String requestJson) {
        System.out.println("Got hotel data generator: " + requestJson);

        RoomUpdateRequest request = JsonReader.readDtoFromJson(requestJson, RoomUpdateRequest.class);


    }

}

