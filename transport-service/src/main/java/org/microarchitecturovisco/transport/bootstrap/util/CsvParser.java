package org.microarchitecturovisco.transport.bootstrap.util;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CsvParser {
    @Autowired
    private LocationRepository locationRepository;
    public List<Location> importLocationsAbroad(String csvFilePath) {
        List<Location> savedLocations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String country = data[4];
                String region = data[5];
                Location location = createNewLocation(country, region);
                if(location != null)
                    savedLocations.add(location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedLocations;
    }

    public List<Location> importLocationsPoland(String csvFilePath) {
        List<Location> savedLocations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String region = data[1];
                if(!Objects.equals(region, "Dojazd własny")){
                    Location location = createNewLocation("Polska", data[1]);
                    if(location != null)
                        savedLocations.add(location);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedLocations;
    }

    private Location createNewLocation(String country, String region) {
        Location location = locationRepository.findByCountryAndRegion(country, region);
        if (location == null) {
            location = new Location(country, region);
            locationRepository.save(location);
            return location;
        }
        return null;
    }

}
