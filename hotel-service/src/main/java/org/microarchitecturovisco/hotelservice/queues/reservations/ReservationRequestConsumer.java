package org.microarchitecturovisco.hotelservice.queues.reservations;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.hotelservice.services.HotelsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationRequestConsumer {

    private final HotelsService hotelsService;

    @RabbitListener(queues = QueuesConfig.QUEUE_HOTEL_BOOK_REQ)
    public String consumeMessageFromQueue(ReservationRequest request) {
        System.out.println("Message received from queue - example: " + request);

        GetHotelsBySearchQueryRequestDto query = GetHotelsBySearchQueryRequestDto.builder()
                .dateFrom(request.getHotelTimeFrom())
                .dateTo(request.getHotelTimeTo())
                .arrivalLocationIds(convertStringsToUUIDs(request.getArrivalLocationIds()))
                .adults(request.getAdultsQuantity())
                .childrenUnderThree(request.getChildrenUnder3Quantity())
                .childrenUnderTen(request.getChildrenUnder10Quantity())
                .childrenUnderEighteen(request.getChildrenUnder18Quantity())
                .build();

        GetHotelsBySearchQueryResponseDto hotels = hotelsService.GetHotelsBySearchQuery(query);

        return Boolean.toString(hotels.getHotels().size() > 0);
    }

    public static List<UUID> convertStringsToUUIDs(List<String> stringList) {
        return stringList.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}
