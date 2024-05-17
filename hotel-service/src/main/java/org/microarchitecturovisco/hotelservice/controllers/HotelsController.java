package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.hotelservice.services.HotelsService;
import org.microarchitecturovisco.hotelservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.microarchitecturovisco.hotelservice.utils.JsonConverter;

@RestController()
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelsService hotelsService;

    @RabbitListener(queues = "hotels.requests.hotelsBySearchQuery")
    public String consumeGetHotelsRequest(String requestDtoJson) {

        GetHotelsBySearchQueryRequestDto requestDto = JsonReader.readGetHotelsBySearchQueryRequestFromJson(requestDtoJson);
        GetHotelsBySearchQueryResponseDto responseDto = hotelsService.GetHotelsBySearchQuery(requestDto);

        System.out.println("Send hotels response size " + responseDto.getHotels().size());


        return JsonConverter.convertGetHotelsBySearchQueryResponseDto(responseDto);
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK_REQ)
    public String consumeMessageFromQueue(CheckHotelAvailabilityRequest request) {
        System.out.println("Message received from queue - example: " + request);

        GetHotelsBySearchQueryRequestDto query = GetHotelsBySearchQueryRequestDto.builder()
                .dateFrom(request.getHotelTimeFrom())
                .dateTo(request.getHotelTimeTo())
                .arrivalLocationIds(request.getArrivalLocationIds())
                .adults(request.getAdultsQuantity())
                .childrenUnderThree(request.getChildrenUnder3Quantity())
                .childrenUnderTen(request.getChildrenUnder10Quantity())
                .childrenUnderEighteen(request.getChildrenUnder18Quantity())
                .build();

        GetHotelsBySearchQueryResponseDto hotels = hotelsService.GetHotelsBySearchQuery(query);

        return Boolean.toString(!hotels.getHotels().isEmpty());
    }
}

