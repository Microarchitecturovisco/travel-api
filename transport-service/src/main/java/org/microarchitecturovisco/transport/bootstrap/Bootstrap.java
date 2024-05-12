package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.bootstrap.util.CsvParser;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {


    private final LocationRepository locationRepository;
    private final TransportCourseRepository transportCourseRepository;
    private final RabbitTemplate rabbitTemplate;

    private final CsvParser csvParser;
    private final String dataDirectory = "transport-service\\src\\main\\java\\org\\microarchitecturovisco\\transport\\bootstrap\\data\\";
    private final TransportRepository transportRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap | Transport");

        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelDepartureOptionsCsvFile = dataDirectory + "hotel_departure_options.csv";
        String transportsSampleCsvFile = dataDirectory + "transports_sample.csv";


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
    private void createBusConnections(Logger logger, List<TransportCourse> busCourses, Map<Integer, List<String>> departureCitiesMap, Map<Integer, Location> hotelLocationMap) {
        List<Location> locationsAvailableByBus = new ArrayList<>();
        locationsAvailableByBus.add(new Location("Albania", "Durres"));
        locationsAvailableByBus.add(new Location("Turcja - narty", "Kayseri"));
        locationsAvailableByBus.add(new Location("Włochy", "Apulia"));
        locationsAvailableByBus.add(new Location("Włochy", "Sycylia"));
        locationsAvailableByBus.add(new Location("Włochy", "Kalabria"));

        for (Map.Entry<Integer, List<String>> entry : departureCitiesMap.entrySet()) {
            int hotelId = entry.getKey();
            List<String> departureCities = entry.getValue();

            // Check if hotelId exists in hotel location map
            if (hotelLocationMap.containsKey(hotelId)) {
                Location hotelLocation = hotelLocationMap.get(hotelId);
                String arrivalCountry = hotelLocation.getCountry();
                String arrivalRegion = hotelLocation.getRegion();
                Location busArrivalLocation = locationRepository.findByCountryAndRegion(arrivalCountry, arrivalRegion);

                // Iterate through each departure city for the current hotel
                for (String departureCity : departureCities) {
                    // Check if the departure city is available by bus
                    for (Location busLocation : locationsAvailableByBus) {
                        // Create transport courses if the departure city matches
                        if (busLocation.getRegion().equals(arrivalRegion)) {
                            Location depLocation = locationRepository.findByCountryAndRegion("Polska", departureCity);
                            // Add bus connection
                            busCourses.add(transportCourseRepository.save(TransportCourse.builder()
                                    .departureFrom(depLocation)
                                    .arrivalAt(busArrivalLocation)
                                    .type(TransportType.BUS).build()));

                            // Add return bus connection
                            busCourses.add(transportCourseRepository.save(TransportCourse.builder()
                                    .departureFrom(busArrivalLocation)
                                    .arrivalAt(depLocation)
                                    .type(TransportType.BUS).build()));

//                            logger.info("BUS Hotel: " + hotelId + " From:" + depLocation.getRegion() + " To:" + arrivalRegion);
                        }
                    }
                }
            } else {
//                logger.info("  - Hotel location not found");
            }
        }
    }

    @RabbitListener(queues = "transports.responses.getTransportsBySearchQuery")
    @RabbitHandler
    public void consumeGetTransportsResponse(GetTransportsBySearchQueryResponseDto responseDto) {

    private void createPlaneConnections(Logger logger, List<TransportCourse> planeCourses, Map<Integer, List<String>> departureCitiesMap, Map<Integer, Location> hotelLocationMap) {
        for (Map.Entry<Integer, List<String>> entry : departureCitiesMap.entrySet()) {
            int hotelId = entry.getKey();
            List<String> departureCities = entry.getValue();

            // Check if hotelId exists in hotel location map
            if (hotelLocationMap.containsKey(hotelId)) {
                Location hotelLocation = hotelLocationMap.get(hotelId);
                String arrivalCountry = hotelLocation.getCountry();
                String arrivalRegion = hotelLocation.getRegion();
                Location planeArrivalLocation = locationRepository.findByCountryAndRegion(arrivalCountry, arrivalRegion);

                // Iterate through each departure city for the current hotel
                for (String departureCity : departureCities) {
                    String departureCountry = "Polska";
                    String departureRegion = departureCity;
                    Location departureLocation = locationRepository.findByCountryAndRegion(departureCountry, departureRegion);

                    // Create transport courses
                    planeCourses.add(transportCourseRepository.save(TransportCourse.builder()
                            .departureFrom(departureLocation)
                            .arrivalAt(planeArrivalLocation)
                            .type(TransportType.PLANE).build()));

                    planeCourses.add(transportCourseRepository.save(TransportCourse.builder()
                            .departureFrom(planeArrivalLocation)
                            .arrivalAt(departureLocation)
                            .type(TransportType.PLANE).build()));

//                    logger.info("PLANE Hotel: " + hotelId + " From:" + departureRegion + " To:" + arrivalRegion);
                }
            } else {
//                logger.info("  - Hotel location not found");
            }
        }

        System.out.println("Received transports of size " + responseDto.getTransportDtoList().size());
    }

}
