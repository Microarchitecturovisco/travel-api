package org.microarchitecturovisco.transport.bootstrap.util.parsers;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Component
public class LocationParser {

    @Autowired
    LocationRepository locationRepository;
    public void importLocationsAbroad(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String country = data[4];
                String region = data[5];
                createNewLocation(locationRepository, country, region);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importLocationsPoland(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String region = data[1];
                if(!Objects.equals(region, "Dojazd własny")){
                    createNewLocation(locationRepository, "Polska", data[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewLocation(LocationRepository locationRepository, String country, String region) {
        Location location = locationRepository.findByCountryAndRegion(country, region);
        if (location == null) {
            location = new Location(country, region);
            locationRepository.save(location);
        }
    }
}
