package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class HotelParser {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HotelRepository hotelRepository;

    public void importHotels(String dataDirectory, PhotosParser photosParser) {
        try (BufferedReader br = new BufferedReader(new FileReader(dataDirectory + "hotels.csv"))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Hotel hotel = createNewHotel(data, photosParser);
                hotelRepository.save(hotel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Hotel createNewHotel(String[] data, PhotosParser photosParser) {
        int hotelId = Integer.parseInt(data[0]);
        String name = data[1];
        String description = data[2];
        float rating = Float.parseFloat(data[3]);
        String country = data[4];
        String region = data[5];

        Location location = locationRepository.findByCountryAndRegion(country, region);
        List<String> photos = photosParser.hotelPhotosMap.getOrDefault(hotelId, Collections.emptyList());

        return new Hotel(hotelId, name, description, rating, location, photos);
    }
}
