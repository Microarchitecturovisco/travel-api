package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class LocationParser {

    public List<LocationDto> importLocations(String csvFilePath) {
        List<LocationDto> locationDtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String country = data[4];
                String region = data[5];
                LocationDto locationDto = createNewLocation(locationDtos, country, region);
                if (locationDto != null) {
                    locationDtos.add(locationDto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationDtos;
    }


    private LocationDto createNewLocation(List<LocationDto> locationDtos, String country, String region) {
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
}
