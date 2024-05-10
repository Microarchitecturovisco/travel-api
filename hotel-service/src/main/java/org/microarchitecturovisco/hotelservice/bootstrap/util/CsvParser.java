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
import java.util.*;
import java.util.logging.Logger;

@Component
public class CsvParser {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HotelRepository hotelRepository;

    private Map<Integer, List<String>> hotelPhotosMap;

    public void importPhotosForHotels(String csvFilePath) {
        Map<Integer, List<String>> hotelPhotosMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String photoUrl = data[1];

                hotelPhotosMap.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(photoUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hotelPhotosMap = hotelPhotosMap;
    }

    public List<Location> importLocations(String csvFilePath) {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                createNewLocation(data);
            }
            locations = locationRepository.findAll();;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locations;
    }

    private void createNewLocation(String[] data) {
        String country = data[4];
        String region = data[5];

        Location location = locationRepository.findByCountryAndRegion(country, region);
        if (location == null) {
            // If location doesn't exist, create a new one
            location = new Location(country, region);
            locationRepository.save(location);
        }
    }

    public List<Hotel> importHotels(String dataDirectory) {
        Logger logger = Logger.getLogger("Bootstrap | Hotels");
        List<Hotel> hotels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataDirectory + "hotels.csv"))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Hotel hotel = createNewHotel(data);
                hotelRepository.save(hotel);
            }
            hotels = hotelRepository.findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hotels;
    }

    private Hotel createNewHotel(String[] data) {
        int id = Integer.parseInt(data[0]);
        String name = data[1];
        String description = data[2];
        float rating = Float.parseFloat(data[3]);
        String country = data[4];
        String region = data[5];

        Location location = locationRepository.findByCountryAndRegion(country, region);
        List<String> photos = hotelPhotosMap.getOrDefault(id, Collections.emptyList());

        return new Hotel(id, name, description, rating, location, photos);
    }
}
