package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.microarchitecturovisco.hotelservice.domain.Location;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class CsvParser {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> importHotels(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | Hotels");
        List<Hotel> hotels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                String description = data[2];
                float rating = Float.parseFloat(data[3]);
                String country = data[4];
                String region = data[5];

                Location location = locationRepository.findByCountryAndRegion(country, region);

                Hotel hotel = new Hotel(id, name, description, rating, location);
                hotelRepository.save(hotel);
            }
            hotels = hotelRepository.findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hotels;
    }

    public List<Location> importLocations(String csvFilePath) {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                String country = data[4];
                String city = data[5];

                createLocation(country, city);
            }
            locations = locationRepository.findAll();;
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
}
