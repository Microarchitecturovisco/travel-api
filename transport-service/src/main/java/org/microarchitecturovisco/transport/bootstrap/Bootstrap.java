package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.bootstrap.util.LocationParser;
import org.microarchitecturovisco.transport.bootstrap.util.TransportCoursesParser;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.dto.TransportCourseDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenMultipleLocationsRequestDto;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.microarchitecturovisco.transport.utils.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final LocationParser locationParser;
    private final TransportCoursesParser transportCoursesParser;
    private final TransportCommandService transportCommandService;
    private final LocationRepository locationRepository;
    private final RabbitTemplate rabbitTemplate;

    public File loadCSVIinitFiles(String filepathInResources)
            throws FileNotFoundException {
        return ResourceUtils.getFile(
                filepathInResources);
    }

    @Override
    public void run(String... args) throws IOException {
        Logger logger = Logger.getLogger("Bootstrap");

        File hotelCsvFile = loadCSVIinitFiles("classpath:initData/hotels.csv");
        File hotelDepartureOptionsCsvFile = loadCSVIinitFiles("classpath:initData/hotel_departure_options.csv");

        List<LocationDto> planeArrivalLocations = locationParser.importLocationsAbroad(hotelCsvFile.getPath(), "PLANE");
        List<LocationDto> busArrivalLocations = locationParser.importLocationsAbroad(hotelCsvFile.getPath(), "BUS");
        List<LocationDto> departureLocations = locationParser.importLocationsPoland(hotelDepartureOptionsCsvFile.getPath());

        Map<String, List<TransportCourseDto>> transportCoursesMap = transportCoursesParser.createTransportCourses(hotelCsvFile.getPath(), hotelDepartureOptionsCsvFile.getPath(), busArrivalLocations, planeArrivalLocations, departureLocations);

        List<TransportCourseDto> planeCourses = transportCoursesMap.get("PLANE");
        List<TransportCourseDto> busCourses = transportCoursesMap.get("BUS");

        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);

        // generate transport for each course and every day of two months
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

//    uncomment to test getMultipleLocations
//    @Scheduled(fixedDelay = 5000, initialDelay = 30000)
    public void test() {
        UUID locationIdA = locationRepository.findFirstByRegion("Gdańsk").getId();
        UUID locationIdB = locationRepository.findFirstByRegion("Katowice").getId();
        UUID locationIdC = locationRepository.findFirstByRegion("Durres").getId();
        UUID locationIdD = locationRepository.findFirstByRegion("Fuerteventura").getId();

        GetTransportsBetweenMultipleLocationsRequestDto requestDto = GetTransportsBetweenMultipleLocationsRequestDto.builder()
                .uuid(java.util.UUID.randomUUID())
                .departureLocationIds(List.of(locationIdA, locationIdB))
                .arrivalLocationIds(List.of(locationIdC, locationIdD))
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 1, 14, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 1, 18, 0, 0))
                .transportType(TransportType.PLANE)
                .adults(2)
                .childrenUnderEighteen(2)
                .childrenUnderTen(0)
                .childrenUnderThree(0)
                .build();

        String response = (String) rabbitTemplate.convertSendAndReceive(
                "transports.requests.getTransportsBetweenMultipleLocations",
                "transports.getTransportsBetweenMultipleLocations",
                JsonConverter.convertToJsonWithLocalDateTime(requestDto));

        System.out.println(response + "\n");
    }
}
