package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.bootstrap.util.CsvParser;
import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.microarchitecturovisco.hotelservice.domain.Location;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final CsvParser csvParser;
    private final String dataDirectory = "hotel-service\\src\\main\\java\\org\\microarchitecturovisco\\hotelservice\\bootstrap\\data\\";

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        // Path to the CSV file
        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelPhotosCsvFile = dataDirectory + "hotel_photos.csv";
        String hotelRoomsCsvFile = dataDirectory + "hotel_rooms.csv";
        String hotelCateringOptionsCsvFile = dataDirectory + "hotel_food_options.csv";


        csvParser.importPhotosForHotels(hotelPhotosCsvFile);
        logger.info("Saved photos");

        csvParser.importLocations(hotelCsvFile);
        logger.info("Saved locations");

        List<Hotel> hotels = csvParser.importHotels(dataDirectory);
        logger.info("Saved hotels: " + hotels);

        csvParser.importRoomsForHotels(hotelRoomsCsvFile);
        logger.info("Saved rooms");

        csvParser.importCateringOptionsForHotels(hotelCateringOptionsCsvFile);
        logger.info("Saved catering options");

    }
}
