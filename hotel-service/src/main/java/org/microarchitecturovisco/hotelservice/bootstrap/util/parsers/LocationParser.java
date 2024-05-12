package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class LocationParser {

    @Autowired
    private LocationRepository locationRepository;


    public void importLocations(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                createNewLocation(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewLocation(String[] data) {
        String country = data[4];
        String region = data[5];

        Location location = locationRepository.findByCountryAndRegion(country, region);
        if (location == null) {
            location = new Location(country, region);
            locationRepository.save(location);
        }
    }
}
