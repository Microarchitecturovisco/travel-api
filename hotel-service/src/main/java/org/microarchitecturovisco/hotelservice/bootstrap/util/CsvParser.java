package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.CateringOptionRepository;
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
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CateringOptionRepository cateringOptionRepository;

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

    public void importRoomsForHotels(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | Rooms");

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
                    logger.info("Hotel not found for room with ID: " + hotelId);
                }

                roomRepository.save(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void importCateringOptionsForHotels(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | CateringOptions");
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String foodOption = data[1];
                float rating = Float.parseFloat(data[2]);

                CateringType cateringType = mapToCateringType(foodOption);
                if (cateringType != null) {
                    float price = calculateCateringPrice(cateringType);
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

                    cateringOptionRepository.save(cateringOption);
                } else {
                    logger.info("Failed to map food option: " + foodOption);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CateringType mapToCateringType(String foodOption) {
        return switch (foodOption) {
            case "All inclusive", "All inclusive ultra", "All inclusive 24h", "All inclusive soft" -> CateringType.ALL_INCLUSIVE;
            case "Full board plus", "Half board plus" -> CateringType.THREE_COURSES;
            case "2 posiłki" -> CateringType.TWO_COURSES;
            case "Śniadania" -> CateringType.BREAKFAST;
            case "Bez wyżywienia" -> CateringType.NO_CATERING;
            default -> null;
        };
    }

    private float calculateCateringPrice(CateringType cateringType) {
        return switch (cateringType) {
            case ALL_INCLUSIVE -> 100.0f;
            case THREE_COURSES -> 80.0f;
            case TWO_COURSES -> 60.0f;
            case BREAKFAST -> 30.0f;
            case NO_CATERING -> 0.0f;
            default -> 0.0f;
        };
    }

}
