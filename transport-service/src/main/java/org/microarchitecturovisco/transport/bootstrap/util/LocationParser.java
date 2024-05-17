package org.microarchitecturovisco.transport.bootstrap.util;

import org.microarchitecturovisco.transport.model.dto.LocationDto;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class LocationParser {

    private final List<Location> locationsAvailableByBus = new ArrayList<>();

    public LocationParser() {
        locationsAvailableByBus.add(new Location("Albania", "Durres"));
        locationsAvailableByBus.add(new Location("Turcja", "Kayseri"));
        locationsAvailableByBus.add(new Location("Włochy", "Apulia"));
        locationsAvailableByBus.add(new Location("Włochy", "Sycylia"));
        locationsAvailableByBus.add(new Location("Włochy", "Kalabria"));
    }

    public List<LocationDto> importLocationsAbroad(String csvFilePath, String transportType) {
        List<LocationDto> locationDtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String country = data[4];
                String region = data[5];
                LocationDto locationDto = createNewLocation(locationDtos, country, region, transportType);
                if (locationDto != null) {
                    locationDtos.add(locationDto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationDtos;
    }

    public List<LocationDto> importLocationsPoland(String csvFilePath) {
        List<LocationDto> locationDtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String region = data[1];
                if (!Objects.equals(region, "Dojazd własny")) {
                    LocationDto locationDto = createNewLocation(locationDtos, "Polska", region, null);
                    if (locationDto != null) {
                        locationDtos.add(locationDto);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationDtos;
    }

    private LocationDto createNewLocation(List<LocationDto> locationDtos, String country, String region, String transportType) {
        if (transportType != null && transportType.equals("BUS")) {
            if (!locationAvailableByBus(country, region)) {
                return null;
            }
        }

        if (locationExists(locationDtos, country, region)) {
            return null;
        }

        return LocationDto.builder()
                .idLocation(UUID.nameUUIDFromBytes((country+region).getBytes()))
                .country(country)
                .region(region)
                .build();
    }

    private boolean locationExists(List<LocationDto> locationDtos, String country, String region) {
        return locationDtos.stream()
                .anyMatch(locationDto -> locationDto.getCountry().equals(country) && locationDto.getRegion().equals(region));
    }

    private boolean locationAvailableByBus(String country, String region) {
        return locationsAvailableByBus.stream()
                .anyMatch(location -> location.getCountry().equals(country) && location.getRegion().equals(region));
    }
}
