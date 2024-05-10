package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.Location;
import org.microarchitecturovisco.hotelservice.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> createLocations(String csvFilePath) {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header line if exists
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t"); // Assuming tab-separated values
                String country = data[4];
                String city = data[5];

                // Retrieve or create location
                createLocation(country, city);

            }
            locations = getAllLocations();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locations;
    }

    private Location createLocation(String country, String region) {
        // Check if location exists in the database
        Location location = locationRepository.findByCountryAndRegion(country, region);
        if (location == null) {
            // If location doesn't exist, create a new one
            location = new Location(country, region);
            locationRepository.save(location);
        }
        return location;
    }

    private List<Location> getAllLocations() {
        // Check if location exists in the database
        return locationRepository.findAll();
    }
}
