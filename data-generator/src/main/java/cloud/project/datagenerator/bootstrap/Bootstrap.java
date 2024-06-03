package cloud.project.datagenerator.bootstrap;

import cloud.project.datagenerator.bootstrap.util.HotelParser;
import cloud.project.datagenerator.bootstrap.util.RoomParser;
import cloud.project.datagenerator.model.domain.Hotel;
import cloud.project.datagenerator.repositories.HotelRepository;
import cloud.project.datagenerator.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final HotelParser hotelParser;
    private final RoomParser roomParser;
    private final ResourceLoader resourceLoader;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) throws IOException {
        Logger logger = Logger.getLogger("Bootstrap");

        Resource hotelCsvFile = resourceLoader.getResource("classpath:initData/hotels.csv");
        Resource hotelRoomsCsvFile = resourceLoader.getResource("classpath:initData/hotel_rooms.csv");

        List<Hotel> hotels = hotelParser.importHotels(hotelCsvFile);
        roomParser.importRooms(hotelRoomsCsvFile, hotels);

        // Save hotels and rooms to the database
        hotels.forEach(hotel -> {
            hotelRepository.save(hotel);
            roomRepository.saveAll(hotel.getRooms());
        });

        logger.info("Hotels and rooms have been imported and saved to the database.");
    }
}
