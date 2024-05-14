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
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.dto.TransportCourseDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;


@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final TransportCommandService transportCommandService;

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

        locationParser.importLocationsAbroad(hotelCsvFile);
        locationParser.importLocationsPoland(hotelDepartureOptionsCsvFile);

        transportCoursesParser.createTransportCourses(hotelCsvFile, hotelDepartureOptionsCsvFile);

        transportParser.importTransports(transportsSampleCsvFile);

        transportReservationParser.importTransportReservations();
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

    public void new_run(String... args) {
        Logger logger = Logger.getLogger("Bootstrap");

        List<LocationDto> departureLocations = new ArrayList<>(List.of(
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Polska").region("Gdańsk").build(),
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Polska").region("Warszawa").build()
        ));

        List<LocationDto> planeArrivalLocations = new ArrayList<>(List.of(
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Egipt").region("Kair").build(),
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Tunezja").region("Tunis").build())
        );

        List<LocationDto> busArrivalLocations = new ArrayList<>(List.of(
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Włochy").region("Florencja").build(),
                LocationDto.builder().idLocation(UUID.randomUUID()).country("Niemcy").region("Berlin").build()
        ));


        List<TransportCourseDto> planeCourses = new ArrayList<>();
        for (LocationDto departureLocation : departureLocations) {
            for (LocationDto planeArrivalLocation : planeArrivalLocations) {
                planeCourses.add(TransportCourseDto.builder()
                        .idTransportCourse(UUID.randomUUID())
                        .departureFromLocation(departureLocation)
                        .arrivalAtLocation(planeArrivalLocation)
                        .type(TransportType.PLANE)
                        .build());
                planeCourses.add(TransportCourseDto.builder()
                        .idTransportCourse(UUID.randomUUID())
                        .departureFromLocation(planeArrivalLocation)
                        .arrivalAtLocation(departureLocation)
                        .type(TransportType.PLANE)
                        .build());
            }
        }

        List<TransportCourseDto> busCourses = new ArrayList<>();
        for (LocationDto departureLocation : departureLocations) {
            for (LocationDto busArrivalLocation : busArrivalLocations) {
                busCourses.add(TransportCourseDto.builder()
                        .idTransportCourse(UUID.randomUUID())
                        .departureFromLocation(departureLocation)
                        .arrivalAtLocation(busArrivalLocation)
                        .type(TransportType.BUS)
                        .build());
                busCourses.add(TransportCourseDto.builder()
                        .idTransportCourse(UUID.randomUUID())
                        .departureFromLocation(busArrivalLocation)
                        .arrivalAtLocation(departureLocation)
                        .type(TransportType.BUS)
                        .build());
            }
        }

        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);

//        generate transport for each course and every day of two months
        for (int day = 0; day < 10; day++) {
            for (TransportCourseDto planeCourse : planeCourses) {
                int capacity = ThreadLocalRandom.current().nextInt(80, 100);

                TransportDto transportDto = TransportDto.builder()
                        .idTransport(UUID.randomUUID())
                        .departureDate(bootstrapBeginDay.plusDays(day))
                        .transportCourse(planeCourse)
                        .capacity(capacity)
                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(100, 500))
                        .build();

                transportCommandService.createTransport(CreateTransportCommand.builder()
                        .uuid(UUID.randomUUID())
                        .commandTimeStamp(LocalDateTime.now())
                        .transportDto(transportDto)
                        .build()
                );

                // make reservations for transport
                int numberOfReservationsToMake = ThreadLocalRandom.current().nextInt(0, 10) > 6 ? capacity : (int) ( capacity * 0.8);

                while (numberOfReservationsToMake > 0) {
                    int occupiedSeats = ThreadLocalRandom.current().nextInt(1, 8);

                    if (numberOfReservationsToMake - occupiedSeats < 0) continue;

                    TransportReservationDto reservationDto = TransportReservationDto.builder()
                            .numberOfSeats(occupiedSeats)
                            .idTransport(transportDto.getIdTransport())
                            .idTransportReservation(UUID.randomUUID())
                            .build();
                    numberOfReservationsToMake -= occupiedSeats;

                    transportCommandService.createReservation(CreateTransportReservationCommand.builder()
                            .uuid(UUID.randomUUID())
                            .commandTimeStamp(LocalDateTime.now())
                            .transportReservationDto(reservationDto)
                            .build()
                    );
                }

            }
            for (TransportCourseDto busCourse : busCourses) {
                TransportDto transportDto = TransportDto.builder()
                        .idTransport(UUID.randomUUID())
                        .transportCourse(busCourse)
                        .departureDate(bootstrapBeginDay.plusDays(day))
                        .capacity(ThreadLocalRandom.current().nextInt(20, 50))
                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(50, 200))
                        .build();

                transportCommandService.createTransport(CreateTransportCommand.builder()
                        .uuid(UUID.randomUUID())
                        .commandTimeStamp(LocalDateTime.now())
                        .transportDto(transportDto)
                        .build()
                );
            }
        }
    }
}

