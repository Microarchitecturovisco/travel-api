package org.microarchitecturovisco.hotelservice.bootstrap.util;


import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Component
public class HotelParser {

    public List<HotelDto> importHotels(String hotelCsvFilePath, String hotelPhotosCsvFilePath, List<LocationDto> hotelLocations) throws IOException {
        Logger logger = Logger.getLogger("HotelDataParser");

        // Load and parse the photos CSV file
        PhotoParser photoParser = new PhotoParser();
        photoParser.importPhotos(hotelPhotosCsvFilePath);

        List<HotelDto> hotelDtos = new ArrayList<>();

        // Read the hotel CSV file and create hotel DTOs
        try (BufferedReader br = new BufferedReader(new FileReader(hotelCsvFilePath))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                HotelDto hotelDto = createHotelDto(data, photoParser, hotelLocations);
                hotelDtos.add(hotelDto);
            }
        } catch (IOException e) {
            logger.severe("Error reading hotel CSV file: " + e.getMessage());
            throw e;
        }

        return hotelDtos;
    }

    private HotelDto createHotelDto(String[] data, PhotoParser photoParser, List<LocationDto> hotelLocations) {
        int hotelId = Integer.parseInt(data[0]);
        String name = data[1];
        String description = data[2];
        float rating = Float.parseFloat(data[3]);
        String country = data[4];
        String region = data[5];

        // Find the LocationDto in the list
        LocationDto location = findLocation(hotelLocations, country, region);
        List<String> photos = photoParser.hotelPhotosMap.getOrDefault(hotelId, Collections.emptyList());

        UUID uuid = UUID.randomUUID(); // Generate random UUID

        return new HotelDto(uuid, name, description, rating, location, null, photos, null);
    }


    private LocationDto findLocation(List<LocationDto> locationDtos, String country, String region) {
        return locationDtos.stream()
                .filter(locationDto -> locationDto.getCountry().equals(country) && locationDto.getRegion().equals(region))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Location not found: " + country + ", " + region));
    }
}
