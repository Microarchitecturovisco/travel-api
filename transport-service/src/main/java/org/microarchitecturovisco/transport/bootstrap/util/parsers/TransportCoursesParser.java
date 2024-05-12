package org.microarchitecturovisco.transport.bootstrap.util.parsers;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Component
public class TransportCoursesParser {

    @Autowired
    private TransportCourseRepository transportCourseRepository;
    @Autowired
    LocationRepository locationRepository;


    public void createTransportCourses(String hotelCsvFile, String hotelDepartureOptionsCsvFile) {
        List<TransportCourse> planeCourses = new ArrayList<>();
        List<TransportCourse> busCourses = new ArrayList<>();

        Map<Integer, List<String>> departureCitiesMap = readDepartureCities(hotelDepartureOptionsCsvFile);
        Map<Integer, Location> hotelLocationMap = readHotelLocations(hotelCsvFile);

        createPlaneConnections(planeCourses, departureCitiesMap, hotelLocationMap);
        createBusConnections(busCourses, departureCitiesMap, hotelLocationMap);
    }

    private static Map<Integer, List<String>> readDepartureCities(String filename) {
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
                    List<String> departureCities = departureCitiesMap.getOrDefault(hotelId, new ArrayList<>());

                    departureCities.add(departureCity);

                    departureCitiesMap.put(hotelId, departureCities);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return departureCitiesMap;
    }


    private static Map<Integer, Location> readHotelLocations(String filename) {
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

    private void createBusConnections(List<TransportCourse> busCourses, Map<Integer, List<String>> departureCitiesMap, Map<Integer, Location> hotelLocationMap) {
        List<Location> locationsAvailableByBus = new ArrayList<>();
        locationsAvailableByBus.add(new Location("Albania", "Durres"));
        locationsAvailableByBus.add(new Location("Turcja - narty", "Kayseri"));
        locationsAvailableByBus.add(new Location("Włochy", "Apulia"));
        locationsAvailableByBus.add(new Location("Włochy", "Sycylia"));
        locationsAvailableByBus.add(new Location("Włochy", "Kalabria"));

        for (Map.Entry<Integer, List<String>> entry : departureCitiesMap.entrySet()) {
            int hotelId = entry.getKey();
            List<String> departureCities = entry.getValue();

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
                        }
                    }
                }
            }
        }
    }


    private void createPlaneConnections(List<TransportCourse> planeCourses, Map<Integer, List<String>> departureCitiesMap, Map<Integer, Location> hotelLocationMap) {
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

                }
            }
        }
    }


}
