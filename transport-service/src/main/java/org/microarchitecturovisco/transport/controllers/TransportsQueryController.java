package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.controllers.reservations.DeleteTransportReservationRequest;
import org.microarchitecturovisco.transport.controllers.reservations.ReservationRequest;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.DeleteTransportReservationCommand;
import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;
import org.microarchitecturovisco.transport.model.dto.request.CheckTransportAvailabilityRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenMultipleLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBetweenLocationsResponseDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.queues.config.QueuesConfig;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.microarchitecturovisco.transport.services.TransportsQueryService;
import org.microarchitecturovisco.transport.utils.json.JsonConverter;
import org.microarchitecturovisco.transport.utils.json.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportsQueryController {

    private final TransportsQueryService transportsQueryService;
    private final RabbitTemplate rabbitTemplate;

    private final TransportCommandService transportCommandService;

    @GetMapping("/")
    public List<TransportDto> getAllTransports() {
        return transportsQueryService.getAllTransports();
    }

    @GetMapping("/locations")
    public List<LocationDto> getLocations() {
        return LocationMapper.mapList(transportsQueryService.getAllLocations());
    }

    @GetMapping("/locations/{region}")
    public LocationDto getLocationByRegionName(
            @PathVariable String region
    ) {
        return LocationMapper.map(transportsQueryService.getLocationByRegionName(region));
    }

    @GetMapping("/available")
    public AvailableTransportsDto getAvailableTransports() {
        return transportsQueryService.getAvailableTransports();
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @RabbitListener(queues = "transports.requests.getTransportsBySearchQuery")
    public String consumeGetTransportsRequest(String requestDtoJson) {
        long startTime = System.currentTimeMillis();

        GetTransportsBySearchQueryRequestDto requestDto = JsonReader.readGetTransportsBySearchQueryRequestFromJson(requestDtoJson);

        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        long endTime = System.currentTimeMillis();
        System.out.println("Send transports response size " + responseDto.getTransportDtoList().size());
        System.out.println("Service call took " + (endTime - startTime) + " ms");

        return JsonConverter.convertGetTransportsBySearchQueryResponseDto(responseDto);
    }

    @RabbitListener(queues = "transports.requests.getTransportsBetweenLocations")
    public String getTransportsBetweenLocations(String requestDtoJson) {
        GetTransportsBetweenLocationsRequestDto requestDto = JsonReader.readGetTransportsBetweenLocationsRequestDtoFromJson(requestDtoJson);

        GetTransportsBetweenLocationsResponseDto responseDto = transportsQueryService.getTransportsBetweenLocations(requestDto);

        return JsonConverter.convertGetTransportsBetweenLocationsResponseDto(responseDto);
    }

    @RabbitListener(queues = "transports.requests.getTransportsBetweenMultipleLocations")
    public String getTransportsBetweenMultipleLocations(String requestDtoJson) {
        GetTransportsBetweenMultipleLocationsRequestDto requestDto = JsonReader.readDtoFromJson(requestDtoJson, GetTransportsBetweenMultipleLocationsRequestDto.class);

        GetTransportsBetweenLocationsResponseDto responseDto = transportsQueryService.getTransportsBetweenMultipleLocations(requestDto);
        return JsonConverter.convertGetTransportsBetweenLocationsResponseDto(responseDto);
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_TRANSPORT_CHECK_AVAILABILITY_REQ)
    public String consumeMessageFromQueueCheckTransportAvailability(String requestDtoJson) {
        System.out.println("Message received from queue requestDtoJson: " + requestDtoJson);
        CheckTransportAvailabilityRequestDto request = JsonReader.readDtoFromJson(requestDtoJson, CheckTransportAvailabilityRequestDto.class);
        System.out.println("Message received from queue: " + request);

        // todo change this method for more detailed query and based on date, transport id, etc

        return "SUCCESS";
    }


    @RabbitListener(queues = "#{handleCreateTransportReservationQueue.name}")
    public void consumeMessageCreateTransportReservation(ReservationRequest request) {
        System.out.println("Message received from queue: " + request);

        int numberOfTransportsInReservation = request.getTransportReservationsIds().size();

        for (int i = 0; i < numberOfTransportsInReservation; i++) {
            UUID idTransport = request.getTransportReservationsIds().get(i);

            int occupiedSeats = request.getAdultsQuantity() +
                    request.getChildrenUnder3Quantity() +
                    request.getChildrenUnder10Quantity() +
                    request.getChildrenUnder18Quantity();

            TransportReservationDto reservationDto = TransportReservationDto.builder()
                    .numberOfSeats(occupiedSeats)
                    .idTransport(idTransport)
                    .idTransportReservation(request.getId())
                    .build();

            transportCommandService.createReservation(CreateTransportReservationCommand.builder()
                    .uuid(reservationDto.getIdTransportReservation())
                    .commandTimeStamp(LocalDateTime.now())
                    .transportReservationDto(reservationDto)
                    .build()
            );
        }
    }

    @RabbitListener(queues = "#{handleDeleteTransportReservationQueue.name}")
    public void consumeMessageDeleteTransportReservation(DeleteTransportReservationRequest request) {
        System.out.println("Message received from queue consumeMessageDeleteTransportReservation: " + request);

        for (UUID transportId : request.getTransportReservationsIds()){
            DeleteTransportReservationCommand command = DeleteTransportReservationCommand.builder()
                    .commandTimeStamp(LocalDateTime.now())
                    .reservationId(request.getId())
                    .transportReservationsId(transportId)
                    .build();

            transportCommandService.deleteReservation(command);
        }
    }
}
