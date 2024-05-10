package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.microarchitecturovisco.hotelservice.domain.Location;
import org.microarchitecturovisco.hotelservice.domain.Room;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.LocationRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CsvParser {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HotelRepository hotelRepository;

    private Map<Integer, List<String>> hotelPhotosMap;
    private Map<Integer, List<Room>> hotelRoomsMap;
    @Autowired
    private RoomRepository roomRepository;

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

    public void importRoomsForHotels(String csvFilePath) {
        Map<Integer, List<Room>> hotelRoomsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String roomName = data[1];
                String description = data[2];
                int guestCapacity = calculateGuestCapacity(description);
                float pricePerAdult = Float.parseFloat(data[3]);

                Room room = Room.builder()
                        .name(roomName)
                        .description(description)
                        .guestCapacity(guestCapacity)
                        .pricePerAdult(pricePerAdult)
                        .build();

                Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
                if (hotel != null) {
                    room.setHotel(hotel);
                } else {
                    System.err.println("Hotel not found for room with ID: " + hotelId);
                }

                roomRepository.save(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hotelRoomsMap = hotelRoomsMap;
    }


    private int calculateGuestCapacity(String description){
        int guestCapacity = 0;

        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            guestCapacity = Integer.parseInt(matcher.group());
        }

        return guestCapacity;
    }

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
        int hotelId = Integer.parseInt(data[0]);
        String name = data[1];
        String description = data[2];
        float rating = Float.parseFloat(data[3]);
        String country = data[4];
        String region = data[5];

        Location location = locationRepository.findByCountryAndRegion(country, region);
        List<String> photos = hotelPhotosMap.getOrDefault(hotelId, Collections.emptyList());

        return new Hotel(hotelId, name, description, rating, location, photos);
    }
}
