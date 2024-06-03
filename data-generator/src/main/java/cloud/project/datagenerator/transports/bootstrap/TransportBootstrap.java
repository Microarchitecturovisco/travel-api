package cloud.project.datagenerator.transports.bootstrap;

import cloud.project.datagenerator.transports.bootstrap.util.LocationParser;
import cloud.project.datagenerator.transports.bootstrap.util.TransportCoursesParser;
import cloud.project.datagenerator.transports.domain.Location;
import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportCourse;
import cloud.project.datagenerator.transports.repositories.LocationRepository;
import cloud.project.datagenerator.transports.repositories.TransportCourseRepository;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import lombok.RequiredArgsConstructor;
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
public class TransportBootstrap implements CommandLineRunner {
    private final LocationParser locationParser;
    private final TransportCoursesParser transportCoursesParser;
    private final ResourceLoader resourceLoader;
    private final TransportRepository transportRepository;
    private final TransportCourseRepository transportCourseRepository;
    private final LocationRepository locationRepository;

    @Override
    public void run(String... args) {
        Logger logger = Logger.getLogger("TransportBootstrap | Transports");

        Resource hotelCsvResource = resourceLoader.getResource("classpath:initData/hotels.csv");
        Resource hotelDepartureOptionsResource = resourceLoader.getResource("classpath:initData/hotel_departure_options.csv");

        List<Location> planeArrivalLocations = locationParser.importLocationsAbroad(hotelCsvResource, "PLANE");
        List<Location> busArrivalLocations = locationParser.importLocationsAbroad(hotelCsvResource, "BUS");
        List<Location> departureLocations = locationParser.importLocationsPoland(hotelDepartureOptionsResource);

        Map<String, List<TransportCourse>> transportCoursesMap = transportCoursesParser.createTransportCourses(hotelCsvResource, hotelDepartureOptionsResource, busArrivalLocations, planeArrivalLocations, departureLocations);

        List<TransportCourse> planeCourses = transportCoursesMap.get("PLANE");
        List<TransportCourse> busCourses = transportCoursesMap.get("BUS");

        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);

        int randomSeed = 12345678;
        Random random = new Random(randomSeed);

        int numberOfDays = 60;

        // generate transport for each course and every day of two months
        for (int day = 0; day < numberOfDays; day++) {
            for (TransportCourse planeCourse : planeCourses) {
                int capacity = random.nextInt(8, 15);

                LocalDateTime departureDate = bootstrapBeginDay.plusDays(day);

                UUID transportId = UUID.nameUUIDFromBytes((
                                  departureDate
                                + planeCourse.toString()
                                + capacity
                                + day).getBytes());

                Transport transport = Transport.builder()
                        .id(transportId)
                        .departureDate(departureDate)
                        .course(planeCourse)
                        .capacity(capacity)
                        .pricePerAdult(random.nextFloat(100, 500))
                        .build();

                locationRepository.saveAll(List.of(planeCourse.getDepartureFrom(), planeCourse.getArrivalAt()));
                transportCourseRepository.save(planeCourse);
                transportRepository.save(transport);
            }

            for (TransportCourse busCourse : busCourses) {
                int capacity = random.nextInt(8, 15);
                LocalDateTime departureDate = bootstrapBeginDay.plusDays(day);

                UUID transportId = UUID.nameUUIDFromBytes((
                                  departureDate
                                + busCourse.toString()
                                + capacity
                                + day).getBytes());

                Transport transport = Transport.builder()
                        .id(transportId)
                        .course(busCourse)
                        .departureDate(departureDate)
                        .capacity(capacity)
                        .pricePerAdult(random.nextFloat(50, 200))
                        .build();

                locationRepository.saveAll(List.of(busCourse.getDepartureFrom(), busCourse.getArrivalAt()));
                transportCourseRepository.save(busCourse);
                transportRepository.save(transport);
            }
            logger.info("TransportBootstrap imported day " + (day + 1) + " out of " + numberOfDays + " days");
        }
        logger.info("TransportBootstrap finished importing data");
    }
}
