package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.bootstrap.util.parsers.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final LocationParser locationParser;
    private final PhotosParser photosParser;
    private final RoomParser roomParser;
    private final HotelParser hotelParser;
    private final RoomReservationParser roomReservationParser;
    private final CateringOptionParser cateringOptionParser;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        String dataDirectory = "hotel-service\\src\\main\\java\\org\\microarchitecturovisco\\hotelservice\\bootstrap\\data\\";
        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelPhotosCsvFile = dataDirectory + "hotel_photos.csv";
        String hotelRoomsCsvFile = dataDirectory + "hotel_rooms.csv";
        String hotelCateringOptionsCsvFile = dataDirectory + "hotel_food_options.csv";
        String roomReservationCsvFile = dataDirectory + "room_reservations.csv";

        photosParser.importPhotos(hotelPhotosCsvFile);
        logger.info("Saved photos");

        locationParser.importLocations(hotelCsvFile);
        logger.info("Saved locations");

        hotelParser.importHotels(dataDirectory, photosParser);
        logger.info("Saved hotels: ");

        roomParser.importRooms(hotelRoomsCsvFile);
        logger.info("Saved rooms");

        cateringOptionParser.importCateringOptions(hotelCateringOptionsCsvFile);
        logger.info("Saved catering options");

        roomReservationParser.importRoomReservations(roomReservationCsvFile);
        logger.info("Saved room reservations");
    }
}
