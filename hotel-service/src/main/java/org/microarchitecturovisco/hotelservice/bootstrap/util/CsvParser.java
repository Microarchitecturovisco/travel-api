package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Component
public class CsvParser {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CateringOptionRepository cateringOptionRepository;

    private Map<Integer, List<String>> hotelPhotosMap;
    @Autowired
    private RoomReservationRepository roomReservationRepository;

    public void importPhotos(String csvFilePath) {
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

    public void importRooms(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | Rooms");
        RoomCapacityCalculator capacityCalculator = new RoomCapacityCalculator();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Room room = createNewRoom(logger, capacityCalculator, data);

                roomRepository.save(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Room createNewRoom(Logger logger, RoomCapacityCalculator capacityCalculator, String[] data) {
        int hotelId = Integer.parseInt(data[0]);
        String roomName = data[1];
        String description = data[2];
        int guestCapacity = capacityCalculator.calculateGuestCapacity(roomName);
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
            logger.info("Hotel not found for room with ID: " + hotelId);
        }
        return room;
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
            location = new Location(country, region);
            locationRepository.save(location);
        }
    }

    public List<Hotel> importHotels(String dataDirectory) {
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

    public void importCateringOptions(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | CateringOptions");
        CateringPriceCalculator cateringPriceCalculator = new CateringPriceCalculator();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                CateringOption cateringOption = createNewCateringOption(logger, cateringPriceCalculator, data);
                cateringOptionRepository.save(cateringOption);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CateringOption createNewCateringOption(Logger logger, CateringPriceCalculator cateringPriceCalculator, String[] data) {
        CateringTypeMapper cateringTypeMapper = new CateringTypeMapper();
        int hotelId = Integer.parseInt(data[0]);
        String foodOption = data[1];
        float rating = Float.parseFloat(data[2]);

        CateringType cateringType = cateringTypeMapper.mapToCateringType(foodOption);
        if (cateringType != null) {
            float price = cateringPriceCalculator.calculateCateringPrice(cateringType);
            CateringOption cateringOption = CateringOption.builder()
                    .type(cateringType)
                    .price(price)
                    .rating(rating)
                    .build();

            Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
            if (hotel != null) {
                cateringOption.setHotel(hotel);
            } else {
                logger.info("Hotel not found for catering option with ID: " + hotelId);
            }
            return cateringOption;
        } else {
            logger.info("Failed to map food option: " + foodOption);
        }
        return new CateringOption();
    }

    public void importRoomReservations(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                RoomReservation roomReservation = createNewRoomReservation(data);
                roomReservationRepository.save(roomReservation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RoomReservation createNewRoomReservation(String[] data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        int roomId = Integer.parseInt(data[0]);
        LocalDateTime dateFrom = LocalDateTime.parse(data[1], formatter);
        LocalDateTime dateTo = LocalDateTime.parse(data[2], formatter);

        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Room room = optionalRoom.orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        return new RoomReservation(dateFrom, dateTo, room);
    }
}
