package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.controllers.reservations.DeleteTransportReservationRequest;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.DeleteTransportReservationCommand;
import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;
import org.microarchitecturovisco.transport.model.dto.request.*;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.CheckTransportAvailabilityResponseDto;
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
import java.util.logging.Logger;

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

        Logger logger = Logger.getLogger("getTransportsBySearchQuery");
        logger.info("Request: " + requestDtoJson);

        GetTransportsBySearchQueryRequestDto requestDto = JsonReader.readGetTransportsBySearchQueryRequestFromJson(requestDtoJson);

        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        logger.info("Response size: " + responseDto.getTransportDtoList().size());

        return JsonConverter.convertGetTransportsBySearchQueryResponseDto(responseDto);
    }

    @RabbitListener(queues = "transports.requests.getTransportsBetweenLocations")
    public String getTransportsBetweenLocations(String requestDtoJson) {
        Logger logger = Logger.getLogger("getTransportsBetweenLocations");
        logger.info("Request: " + requestDtoJson);

        GetTransportsBetweenLocationsRequestDto requestDto = JsonReader.readGetTransportsBetweenLocationsRequestDtoFromJson(requestDtoJson);

        GetTransportsBetweenLocationsResponseDto responseDto = transportsQueryService.getTransportsBetweenLocations(requestDto);

        logger.info("Response size: " + responseDto.getTransportPairs().size());

        return JsonConverter.convertGetTransportsBetweenLocationsResponseDto(responseDto);
    }

    @RabbitListener(queues = "transports.requests.getTransportsBetweenMultipleLocations")
    public String getTransportsBetweenMultipleLocations(String requestDtoJson) {
        Logger logger = Logger.getLogger("getTransportsBetweenMultipleLocations");
        logger.info("Request: " + requestDtoJson);

        GetTransportsBetweenMultipleLocationsRequestDto requestDto = JsonReader.readDtoFromJson(requestDtoJson, GetTransportsBetweenMultipleLocationsRequestDto.class);

        GetTransportsBetweenLocationsResponseDto responseDto = transportsQueryService.getTransportsBetweenMultipleLocations(requestDto);

        logger.info("Response size: " + responseDto.getTransportPairs().size());

        return JsonConverter.convertGetTransportsBetweenLocationsResponseDto(responseDto);
    }

    @RabbitListener(queues = QueuesConfig.QUEUE_TRANSPORT_CHECK_AVAILABILITY_REQ)
    public String consumeMessageFromQueueCheckTransportAvailability(String requestDtoJson) {
        System.out.println("Message received from queue requestDtoJson: " + requestDtoJson);
        CheckTransportAvailabilityRequestDto request = JsonReader.readDtoFromJson(requestDtoJson, CheckTransportAvailabilityRequestDto.class);
        System.out.println("Message received from queue: " + request);

//        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
//
//                .build();

        // todo implement method for more detailed query and based on date, transport id, etc
//        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        CheckTransportAvailabilityResponseDto response = CheckTransportAvailabilityResponseDto.builder()
                .ifAvailable(true)
                .build();

        System.out.println("Response to convert:" + response );
        String responseJson = JsonConverter.convertToJsonWithLocalDateTime(response);
        System.out.println("Response after conversion:" + responseJson );

        return responseJson;
    }


    @RabbitListener(queues = "#{handleCreateTransportReservationQueue.name}")
    public void consumeMessageCreateTransportReservation(String requestDtoJson) {
        CreateTransportReservationRequest request = JsonReader.readDtoFromJson(requestDtoJson, CreateTransportReservationRequest.class);

        System.out.println("Message received from queue request: " + request);

        for (UUID idTransport: request.getTransportIds()) {

            TransportReservationDto reservationDto = TransportReservationDto.builder()
                    .numberOfSeats(request.getAmountOfQuests())
                    .idTransport(idTransport)
                    .reservationId(request.getReservationId())
                    .build();

            CreateTransportReservationCommand command = CreateTransportReservationCommand.builder()
                    .uuid(UUID.randomUUID())
                    .commandTimeStamp(LocalDateTime.now())
                    .transportReservationDto(reservationDto)
                    .build();

            transportCommandService.createReservation(command);
            System.out.println("reservationDto: " + reservationDto);
        }
    }

    @RabbitListener(queues = "#{handleDeleteTransportReservationQueue.name}")
    public void consumeMessageDeleteTransportReservation(String requestJson) {
        DeleteTransportReservationRequest request = JsonReader.readDtoFromJson(requestJson, DeleteTransportReservationRequest.class);

        System.out.println("Message received from queue consumeMessageDeleteTransportReservation: " + request);

        for (UUID transportId : request.getTransportReservationsIds()){
            DeleteTransportReservationCommand command = DeleteTransportReservationCommand.builder()
                    .commandTimeStamp(LocalDateTime.now())
                    .reservationId(request.getReservationId())
                    .transportId(transportId)
                    .build();

            transportCommandService.deleteReservation(command);
        }
    }
}
