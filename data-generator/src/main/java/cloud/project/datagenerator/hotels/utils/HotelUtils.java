package cloud.project.datagenerator.hotels.utils;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class HotelUtils {

    private final HotelRepository hotelRepository;
    private final Random random = new Random();
    Logger logger = Logger.getLogger("DataGenerator | Hotels");

    public Hotel getRandomHotel() {
        List<Hotel> hotels = hotelRepository.findAll();

        if (hotels.isEmpty()) {
            logger.info("No hotels found.");
            return null;
        }

        return hotels.get(random.nextInt(5));
    }

    public Room getRandomRoomFromHotel(Hotel hotel) {
        List<Room> rooms = hotel.getRooms();

        if (rooms.isEmpty()) {
            logger.info("No rooms found in the selected hotel.");
            return null;
        }

        return rooms.get(random.nextInt(rooms.size()));
    }
}
