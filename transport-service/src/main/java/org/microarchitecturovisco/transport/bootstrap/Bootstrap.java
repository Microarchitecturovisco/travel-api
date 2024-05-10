package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {


    private final LocationRepository locationRepository;
    private final TransportRepository transportRepository;
    private final TransportCourseRepository transportCourseRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        List<Location> departureLocations = new ArrayList<>();

        List<Location> planeArrivalLocations = new ArrayList<>();

        List<Location> busArrivalLocations = new ArrayList<>();

        for (Location departureLocation : List.of(
                Location.builder().country("Polska").region("Gdańsk").build(),
                Location.builder().country("Polska").region("Warszawa").build(),
                Location.builder().country("Polska").region("Poznań").build()
        )) {
            departureLocations.add(locationRepository.save(departureLocation));
        }

        for (Location planeArrivalLocation : List.of(
                Location.builder().country("Egipt").region("Kair").build(),
                Location.builder().country("RPA").region("Kapsztad").build(),
                Location.builder().country("Tunezja").region("Tunis").build())) {
            planeArrivalLocations.add(locationRepository.save(planeArrivalLocation));
        }

        for (Location busArrivalLocation : List.of(
                Location.builder().country("Włochy").region("Florencja").build(),
                Location.builder().country("Włochy").region("Wenecja").build(),
                Location.builder().country("Niemcy").region("Hanower").build(),
                Location.builder().country("Niemcy").region("Berlin").build()
        )) {
            busArrivalLocations.add(locationRepository.save(busArrivalLocation));
        }


        List<TransportCourse> planeCourses = new ArrayList<>();
        for (Location departureLocation : departureLocations) {
            for (Location planeArrivalLocation : planeArrivalLocations) {
                planeCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(departureLocation).arrivalAt(planeArrivalLocation).type(TransportType.PLANE).build()));
            }
        }
        for (Location departureLocation : departureLocations) {
            for (Location planeArrivalLocation : planeArrivalLocations) {
                planeCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(planeArrivalLocation).arrivalAt(departureLocation).type(TransportType.PLANE).build()));
            }
        }

        List<TransportCourse> busCourses = new ArrayList<>();
        for (Location departureLocation : departureLocations) {
            for (Location busArrivalLocation : busArrivalLocations) {
                busCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(departureLocation).arrivalAt(busArrivalLocation).type(TransportType.BUS).build()));
            }
        }
        for (Location departureLocation : departureLocations) {
            for (Location busArrivalLocation : busArrivalLocations) {
                busCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(busArrivalLocation).arrivalAt(departureLocation).type(TransportType.BUS).build()));
            }
        }

        List<Transport> transports = new ArrayList<>();

        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);

        // generate transport for each course and every day of two months
        for (int day = 0; day < 60; day++) {
            for (TransportCourse planeCourse : planeCourses) {
                int capacity = ThreadLocalRandom.current().nextInt(80, 100);

                Transport transport = Transport.builder()
                        .course(planeCourse)
                        .departureDate(bootstrapBeginDay.plusDays(day))
                        .capacity(capacity)
                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(100, 500))
                        .build();
                transport = transportRepository.save(transport);

                int numberOfReservationsToMake = ThreadLocalRandom.current().nextInt(0, 10) > 6 ? capacity : (int) ( capacity * 0.8);

                List<TransportReservation> reservations = new ArrayList<>();

                while (numberOfReservationsToMake > 0) {
                    int occupiedSeats = ThreadLocalRandom.current().nextInt(1, 8);

                    if (numberOfReservationsToMake - occupiedSeats < 0) continue;

                    TransportReservation reservation = TransportReservation.builder()
                            .numberOfSeats(occupiedSeats)
                            .transport(transport)
                            .build();
                    reservations.add(reservation);
                    numberOfReservationsToMake -= occupiedSeats;
                }
                transport.setTransportReservations(reservations);
                transports.add(transportRepository.save(transport));
            }
            for (TransportCourse busCourse : busCourses) {
                Transport transport = Transport.builder()
                        .course(busCourse)
                        .departureDate(bootstrapBeginDay.plusDays(day))
                        .capacity(ThreadLocalRandom.current().nextInt(20, 50))
                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(50, 200))
                        .build();
                transports.add(transportRepository.save(transport));
            }
        }



    }

    //@Scheduled(fixedDelay = 10000)
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

//    @RabbitListener(queues = "transports.responses.getTransportsBySearchQuery")
//    @RabbitHandler
//    public void consumeGetTransportsResponse(GetTransportsBySearchQueryResponseDto responseDto) {
//
//        System.out.println("Received transports:");
//        for (TransportDto transportDto : responseDto.getTransportDtoList()) {
//            System.out.println("  - " + transportDto.getTransportCourse().getDepartureFromLocation().getRegion() + " <-> " + transportDto.getTransportCourse().getArrivalAtLocation().getRegion());
//            System.out.println("    " + transportDto.getDepartureDate());
//        }
//
//        System.out.println("Received transports of size " + responseDto.getTransportDtoList().size());
//    }

}
