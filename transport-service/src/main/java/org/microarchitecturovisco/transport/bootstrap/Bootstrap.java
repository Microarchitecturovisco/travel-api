package org.microarchitecturovisco.transport.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportReservationParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.LocationParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportCoursesParser;
import org.microarchitecturovisco.transport.bootstrap.util.parsers.TransportParser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    private final TransportReservationParser transportReservationParser;
    private final String dataDirectory = "transport-service\\src\\main\\java\\org\\microarchitecturovisco\\transport\\bootstrap\\data\\";

    @Override
    public void run(String... args) throws Exception {
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
                            .arrivalAt(departureLocation)
                            .type(TransportType.PLANE).build()));


        System.out.println("Received transports of size " + responseDto.getTransportDtoList().size());
    }

}
