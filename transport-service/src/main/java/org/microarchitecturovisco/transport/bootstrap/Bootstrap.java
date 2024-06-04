package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.bootstrap.util.LocationParser;
import org.microarchitecturovisco.transport.bootstrap.util.TransportCoursesParser;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.dto.TransportCourseDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenMultipleLocationsRequestDto;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.microarchitecturovisco.transport.utils.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final LocationParser locationParser;
    private final TransportCoursesParser transportCoursesParser;
    private final TransportCommandService transportCommandService;
    private final LocationRepository locationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) {
        Logger logger = Logger.getLogger("Bootstrap | Transports");

        Resource hotelCsvResource = resourceLoader.getResource("classpath:initData/hotels.csv");
        Resource hotelDepartureOptionsResource = resourceLoader.getResource("classpath:initData/hotel_departure_options.csv");

        List<LocationDto> planeArrivalLocations = locationParser.importLocationsAbroad(hotelCsvResource, "PLANE");
        List<LocationDto> busArrivalLocations = locationParser.importLocationsAbroad(hotelCsvResource, "BUS");
        List<LocationDto> departureLocations = locationParser.importLocationsPoland(hotelDepartureOptionsResource);

        Map<String, List<TransportCourseDto>> transportCoursesMap = transportCoursesParser.createTransportCourses(hotelCsvResource, hotelDepartureOptionsResource, busArrivalLocations, planeArrivalLocations, departureLocations);

        List<TransportCourseDto> planeCourses = transportCoursesMap.get("PLANE");
        List<TransportCourseDto> busCourses = transportCoursesMap.get("BUS");

        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);

        int randomSeed = 12345678;
        Random random = new Random(randomSeed);

        int numberOfDays = 60;

        // generate transport for each course and every day of two months
        for (int day = 0; day < numberOfDays; day++) {
            for (TransportCourseDto planeCourse : planeCourses) {
                int capacity = random.nextInt(8, 15);

                LocalDateTime departureDate = bootstrapBeginDay.plusDays(day);

                UUID transportId = UUID.nameUUIDFromBytes((
                                  departureDate
                                + planeCourse.getIdTransportCourse().toString()
                                + capacity
                                + day).getBytes());

                TransportDto transportDto = TransportDto.builder()
                        .idTransport(transportId)
                        .departureDate(departureDate)
                        .transportCourse(planeCourse)
                        .capacity(capacity)
                        .pricePerAdult(random.nextFloat(100, 500))
                        .build();

                transportCommandService.createTransport(CreateTransportCommand.builder()
                        .uuid(transportId)
                        .commandTimeStamp(LocalDateTime.now())
                        .transportDto(transportDto)
                        .build()
                );

                createReservationsForTransport(planeCourse, capacity, transportDto);
            }

            for (TransportCourseDto busCourse : busCourses) {
                int capacity = random.nextInt(8, 15);
                LocalDateTime departureDate = bootstrapBeginDay.plusDays(day);

                UUID transportId = UUID.nameUUIDFromBytes((
                                  departureDate
                                + busCourse.getIdTransportCourse().toString()
                                + capacity
                                + day).getBytes());

                TransportDto transportDto = TransportDto.builder()
                        .idTransport(transportId)
                        .transportCourse(busCourse)
                        .departureDate(departureDate)
                        .capacity(capacity)
                        .pricePerAdult(random.nextFloat(50, 200))
                        .build();

                transportCommandService.createTransport(CreateTransportCommand.builder()
                        .uuid(transportId)
                        .commandTimeStamp(LocalDateTime.now())
                        .transportDto(transportDto)
                        .build()
                );
            }
            logger.info("Bootstrap imported day " + (day + 1) + " out of " + numberOfDays + " days");
        }
        logger.info("Bootstrap finished importing data");
    }

    private void createReservationsForTransport(TransportCourseDto planeCourse, int capacity, TransportDto transportDto) {
        int randomSeed = 12345678;
        Random randomReservations = new Random(randomSeed);

        int numberOfReservationsToMake = randomReservations.nextInt(0, 10) > 7 ? capacity : (int) (capacity * 0.6);

        while (numberOfReservationsToMake > 0) {
            int occupiedSeats = randomReservations.nextInt(1, 8);

            if (numberOfReservationsToMake - occupiedSeats < 0) continue;

            UUID transportReservationId = UUID.nameUUIDFromBytes((
                            planeCourse.getIdTransportCourse().toString()
                            + capacity
                            + transportDto.getIdTransport()
                            + numberOfReservationsToMake).getBytes());

            TransportReservationDto reservationDto = TransportReservationDto.builder()
                    .numberOfSeats(occupiedSeats)
                    .idTransport(transportDto.getIdTransport())
                    .reservationId(transportReservationId)
                    .build();

            numberOfReservationsToMake -= occupiedSeats;

            transportCommandService.createReservation(CreateTransportReservationCommand.builder()
                    .uuid(transportReservationId)
                    .commandTimeStamp(LocalDateTime.now())
                    .transportReservationDto(reservationDto)
                    .build()
            );
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
