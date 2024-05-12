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

        List<Location> allAbroadLocations = csvParser.importLocationsAbroad(hotelCsvFile);
        List<Location> allPolishLocations = csvParser.importLocationsPoland(hotelDepartureOptionsCsvFile);
        logger.info("Saved locations: " + locationRepository.count());

        createTransportCourses(logger, allAbroadLocations, allPolishLocations);

        createTransports(logger, transportsSampleCsvFile);

        ///////////////////////////////////
//
//        List<TransportCourse> planeCourses = new ArrayList<>();
//        for (Location departureLocation : departureLocations) {
//            for (Location planeArrivalLocation : planeArrivalLocations) {
//                planeCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(departureLocation).arrivalAt(planeArrivalLocation).type(TransportType.PLANE).build()));
//            }
//        }
//        for (Location departureLocation : departureLocations) {
//            for (Location planeArrivalLocation : planeArrivalLocations) {
//                planeCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(planeArrivalLocation).arrivalAt(departureLocation).type(TransportType.PLANE).build()));
//            }
//        }
//
//        List<TransportCourse> busCourses = new ArrayList<>();
//        for (Location departureLocation : departureLocations) {
//            for (Location busArrivalLocation : busArrivalLocations) {
//                busCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(departureLocation).arrivalAt(busArrivalLocation).type(TransportType.BUS).build()));
//            }
//        }
//        for (Location departureLocation : departureLocations) {
//            for (Location busArrivalLocation : busArrivalLocations) {
//                busCourses.add(transportCourseRepository.save(TransportCourse.builder().departureFrom(busArrivalLocation).arrivalAt(departureLocation).type(TransportType.BUS).build()));
//            }
//        }
//
//        List<Transport> transports = new ArrayList<>();
//
//        LocalDateTime bootstrapBeginDay = LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0);
//
//        // generate transport for each course and every day of two months
//        for (int day = 0; day < 60; day++) {
//            for (TransportCourse planeCourse : planeCourses) {
//                int capacity = ThreadLocalRandom.current().nextInt(80, 100);
//
//                Transport transport = Transport.builder()
//                        .course(planeCourse)
//                        .departureDate(bootstrapBeginDay.plusDays(day))
//                        .capacity(capacity)
//                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(100, 500))
//                        .build();
//                transport = transportRepository.save(transport);
//
//                int numberOfReservationsToMake = ThreadLocalRandom.current().nextInt(0, 10) > 6 ? capacity : (int) ( capacity * 0.8);
//
//                List<TransportReservation> reservations = new ArrayList<>();
//
//                while (numberOfReservationsToMake > 0) {
//                    int occupiedSeats = ThreadLocalRandom.current().nextInt(1, 8);
//
//                    if (numberOfReservationsToMake - occupiedSeats < 0) continue;
//
//                    TransportReservation reservation = TransportReservation.builder()
//                            .numberOfSeats(occupiedSeats)
//                            .transport(transport)
//                            .build();
//                    reservations.add(reservation);
//                    numberOfReservationsToMake -= occupiedSeats;
//                }
//                transport.setTransportReservations(reservations);
//                transports.add(transportRepository.save(transport));
//            }
//            for (TransportCourse busCourse : busCourses) {
//                Transport transport = Transport.builder()
//                        .course(busCourse)
//                        .departureDate(bootstrapBeginDay.plusDays(day))
//                        .capacity(ThreadLocalRandom.current().nextInt(20, 50))
//                        .pricePerAdult(ThreadLocalRandom.current().nextFloat(50, 200))
//                        .build();
//                transports.add(transportRepository.save(transport));
//            }
//        }
    }

    private void createTransports(Logger logger, String csvfilename) {
        csvParser.importTransports(csvfilename);
        logger.info("Saved transports: " + transportRepository.count());
    }

    public static Map<Integer, List<String>> readDepartureCities(String filename) {
        Map<Integer, List<String>> departureCitiesMap = new HashMap<>();

        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            scanner.nextLine(); // Skip header line

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String departureCity = data[1];

                // Exclude "Dojazd własny" departure city
                if (!departureCity.equals("Dojazd własny")) {
                    // Get the list of departure cities for the current hotel ID
                    List<String> departureCities = departureCitiesMap.getOrDefault(hotelId, new ArrayList<>());

                    // Add the current departure city to the list
                    departureCities.add(departureCity);

                    // Update the map with the list of departure cities for the current hotel ID
                    departureCitiesMap.put(hotelId, departureCities);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return departureCitiesMap;
    }


    public static Map<Integer, Location> readHotelLocations(String filename) {
        Map<Integer, Location> hotelLocationMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String country = data[4];
                String city = data[5];

                hotelLocationMap.put(hotelId, new Location(country, city));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hotelLocationMap;
    }
    private void createTransportCourses(Logger logger, List<Location> allAbroadLocations, List<Location> allPolishLocations) {
        List<TransportCourse> planeCourses = new ArrayList<>();
        List<TransportCourse> busCourses = new ArrayList<>();
        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelDepartureOptionsCsvFile = dataDirectory + "hotel_departure_options.csv";

        Map<Integer, List<String>> departureCitiesMap = readDepartureCities(hotelDepartureOptionsCsvFile);
        Map<Integer, Location> hotelLocationMap = readHotelLocations(hotelCsvFile);

        createPlaneConnections(logger, planeCourses, departureCitiesMap, hotelLocationMap);
        createBusConnections(logger, busCourses, departureCitiesMap, hotelLocationMap);
        logger.info("Saved transport courses: " + transportCourseRepository.count());
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
