package cloud.project.datagenerator.hotels.bootstrap.util;

import cloud.project.datagenerator.hotels.bootstrap.util.hotel.HotelCsvReader;
import cloud.project.datagenerator.hotels.bootstrap.util.room.RoomCapacityCalculator;
import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class RoomParser {

    private final HotelCsvReader hotelCsvReader;

    public void importRooms(Resource resource, List<Hotel> hotels) {
        Logger logger = Logger.getLogger("Bootstrap | Rooms");
        RoomCapacityCalculator capacityCalculator = new RoomCapacityCalculator();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Room room = createNewRoom(logger, capacityCalculator, data, hotels);

                // Add room to the corresponding hotel
                if (room != null) {
                    Optional<Hotel> hotelOpt = hotels.stream()
                            .filter(hotel -> hotel.getId().equals(room.getHotel().getId()))
                            .findFirst();
                    hotelOpt.ifPresent(hotel -> hotel.getRooms().add(room));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Room createNewRoom(Logger logger, RoomCapacityCalculator capacityCalculator, String[] data, List<Hotel> hotels) throws FileNotFoundException {
        int hotelId = Integer.parseInt(data[0]);
        String roomName = data[1];
        String description = data[2];
        int guestCapacity = capacityCalculator.calculateGuestCapacity(roomName);
        float pricePerAdult = Float.parseFloat(data[3]);
        UUID roomId = UUID.fromString((data[4]));

        Optional<Hotel> hotelOpt = searchForHotel(hotels, hotelId);

        if (hotelOpt.isPresent()) {
            return Room.builder()
                    .id(roomId)
                    .hotel(hotelOpt.get())
                    .name(roomName)
                    .guestCapacity(guestCapacity)
                    .pricePerAdult(pricePerAdult)
                    .description(description)
                    .build();
        } else {
            logger.info("Hotel not found for room with ID: " + hotelId);
            return null;
        }
    }

    private Optional<Hotel> searchForHotel(List<Hotel> hotels, int hotelId) throws FileNotFoundException {
        // Retrieve hotel name from hotels.csv based on hotelId
        String hotelName = hotelCsvReader.getHotelNameById(hotelId);

        // Check if the hotel exists in the provided list
        return hotels.stream()
                .filter(hotel -> hotel.getName().equals(hotelName))
                .findFirst();
    }

}
