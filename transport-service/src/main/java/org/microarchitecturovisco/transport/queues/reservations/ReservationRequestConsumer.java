package org.microarchitecturovisco.transport.queues.reservations;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.queues.config.QueuesConfig;
import org.microarchitecturovisco.transport.services.TransportsQueryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationRequestConsumer {
    private final TransportsQueryService transportsQueryService;

    @RabbitListener(queues = QueuesConfig.QUEUE_TRANSPORT_BOOK_REQ)
    public String consumeMessageFromQueue(ReservationRequest request) {
        System.out.println("Message received from queue: " + request);

        GetTransportsBySearchQueryRequestDto searchQuery = createSearchQuery(request);
        GetTransportsBySearchQueryResponseDto transports = transportsQueryService.getTransportsBySearchQuery(searchQuery);

        System.out.println("TRANSPORTS: " + transports.toString());

        if(transports.getTransportDtoList().size()>0){
            return Boolean.toString(true);
        }
        else{
            return Boolean.toString(false);
        }
    }

    private static GetTransportsBySearchQueryRequestDto createSearchQuery(ReservationRequest request) {
        GetTransportsBySearchQueryRequestDto searchQuery = GetTransportsBySearchQueryRequestDto.builder()
                .uuid("eeeeeeee-bbbb-12d3-a456-426614174000")
                .dateFrom(request.getHotelTimeFrom())
                .dateTo(request.getHotelTimeTo())
                .departureLocationIdsByPlane(convertStringsToUUIDs(request.getDepartureLocationIdsByPlane()))
                .departureLocationIdsByBus(convertStringsToUUIDs(request.getDepartureLocationIdsByBus()))
                .arrivalLocationIds(convertStringsToUUIDs(request.getArrivalLocationIds()))
                .adults(request.getAdultsQuantity())
                .childrenUnderThree(request.getChildrenUnder3Quantity())
                .childrenUnderTen(request.getChildrenUnder10Quantity())
                .childrenUnderEighteen(request.getChildrenUnder18Quantity())
                .build();

        return searchQuery;
    }
    public static List<UUID> convertStringsToUUIDs(List<String> stringList) {
        return stringList.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}
