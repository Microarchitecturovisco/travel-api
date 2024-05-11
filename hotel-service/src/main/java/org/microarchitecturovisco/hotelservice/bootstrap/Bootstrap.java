package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.bootstrap.util.CsvParser;
import org.microarchitecturovisco.hotelservice.domain.Hotel;
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

        importScrappedDataFromCsvFiles(logger);

        createSampleRoomReservations(logger);
    }

    private void createSampleRoomReservations(Logger logger) {
        String roomReservationCsvFile = dataDirectory + "room_reservations.csv";
        csvParser.importRoomReservations(roomReservationCsvFile);
        logger.info("Saved room reservations");
    }

    private void importScrappedDataFromCsvFiles(Logger logger) {
        String hotelCsvFile = dataDirectory + "hotels.csv";
        String hotelPhotosCsvFile = dataDirectory + "hotel_photos.csv";
        String hotelRoomsCsvFile = dataDirectory + "hotel_rooms.csv";
        String hotelCateringOptionsCsvFile = dataDirectory + "hotel_food_options.csv";

        csvParser.importPhotos(hotelPhotosCsvFile);
        logger.info("Saved photos");

        csvParser.importLocations(hotelCsvFile);
        logger.info("Saved locations");

        List<Hotel> hotels = csvParser.importHotels(dataDirectory);
        logger.info("Saved hotels: ");

        csvParser.importRooms(hotelRoomsCsvFile);
        logger.info("Saved rooms");

        csvParser.importCateringOptions(hotelCateringOptionsCsvFile);
        logger.info("Saved catering options");
    }
}
