package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CreateHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.DeleteHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.DeleteRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.CheckHotelAvailabilityQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelDetailsRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
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
    public String consumeMessageCheckHotelAvailability(CheckHotelAvailabilityRequest request) {
        System.out.println("Message received from queue: " + request);

        CheckHotelAvailabilityQueryRequestDto query = CheckHotelAvailabilityQueryRequestDto.builder()
                .dateFrom(request.getHotelTimeFrom())
                .dateTo(request.getHotelTimeTo())
                .adults(request.getAdultsQuantity())
                .childrenUnderThree(request.getChildrenUnder3Quantity())
                .childrenUnderTen(request.getChildrenUnder10Quantity())
                .childrenUnderEighteen(request.getChildrenUnder18Quantity())
                .hotelId(request.getHotelId())
                .roomReservationsIds(request.getRoomReservationsIds())
                .build();

        boolean hotelAvailable = hotelsService.CheckHotelAvailability(query);

        return String.valueOf(hotelAvailable) ;
    }

    @RabbitListener(queues = "#{handleCreateHotelReservationQueue.name}")
    public void consumeMessageCreateHotelReservation(CreateHotelReservationRequest createHotelReservationRequest) {
        System.out.println("Message received from queue createHotelReservationRequest: " + createHotelReservationRequest);
        System.out.println("Reservation id: " + createHotelReservationRequest.getId());

        int numberOfRoomsInReservation = createHotelReservationRequest.getRoomReservationsIds().size();

        List<RoomReservationDto> roomReservations = new ArrayList<>();

        for (int i = 0; i < numberOfRoomsInReservation; i++) {
            UUID roomId = createHotelReservationRequest.getRoomReservationsIds().get(i);

            RoomReservationDto roomReservation = new RoomReservationDto();
            roomReservation.setReservationId(createHotelReservationRequest.getId());
            roomReservation.setDateFrom(createHotelReservationRequest.getHotelTimeFrom());
            roomReservation.setDateTo(createHotelReservationRequest.getHotelTimeTo());
            roomReservation.setHotelId(createHotelReservationRequest.getHotelId());
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
    public void consumeMessageDeleteHotelReservation(DeleteHotelReservationRequest request) {
        System.out.println("Message received from queue DeleteHotelReservationRequest: " + request);

        // TODO: delete the reservation using fields in request:
        //    private UUID reservationId;
        //    private UUID hotelId;
        //    private List<UUID> roomIds;

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
}

