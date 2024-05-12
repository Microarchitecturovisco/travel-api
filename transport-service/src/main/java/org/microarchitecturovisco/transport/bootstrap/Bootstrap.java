package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportReservationParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.LocationParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportCoursesParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportParser;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;


@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    private final TransportReservationParser transportReservationParser;
    private final LocationParser locationParser;
    private final TransportParser transportParser;
    private final TransportCoursesParser transportCoursesParser;

    @Override
    public void run(String... args) throws Exception {
        String dataDirectory = "transport-service\\src\\main\\java\\org\\microarchitecturovisco\\transport\\bootstrap\\data\\";
        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelDepartureOptionsCsvFile = dataDirectory + "hotel_departure_options.csv";
        String transportsSampleCsvFile = dataDirectory + "transports_sample.csv";
        String transportReservationSampleCsvFile = dataDirectory + "transport_reservation_sample.csv";

        locationParser.importLocationsAbroad(hotelCsvFile);
        locationParser.importLocationsPoland(hotelDepartureOptionsCsvFile);

        transportCoursesParser.createTransportCourses(hotelCsvFile, hotelDepartureOptionsCsvFile);

        transportParser.importTransports(transportsSampleCsvFile);

        transportReservationParser.importTransportReservations(transportReservationSampleCsvFile);
    }

    @Scheduled(fixedDelay = 10000)
    public void testGetTransportsBySearchQuery() {
        GetTransportsBySearchQueryRequestDto testRequestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 14, 12, 0, 0))
                .departureLocationIdsByPlane(List.of(1))
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(List.of(6))
                .adults(2)
                .childrenUnderThree(1)
                .childrenUnderTen(1)
                .childrenUnderEighteen(1)
                .build();

        rabbitTemplate.convertAndSend("transports.requests.getTransportsBySearchQuery", testRequestDto);
    }

    @RabbitListener(queues = "transports.responses.getTransportsBySearchQuery")
    @RabbitHandler
    public void consumeGetTransportsResponse(GetTransportsBySearchQueryResponseDto responseDto) {

        System.out.println("Received transports:");
        for (TransportDto transportDto : responseDto.getTransportDtoList()) {
            System.out.println("  - " + transportDto.getTransportCourse().getDepartureFromLocation().getRegion() + " <-> " + transportDto.getTransportCourse().getArrivalAtLocation().getRegion());
            System.out.println("    " + transportDto.getDepartureDate());
        }

        System.out.println("Received transports of size " + responseDto.getTransportDtoList().size());
    }
}

