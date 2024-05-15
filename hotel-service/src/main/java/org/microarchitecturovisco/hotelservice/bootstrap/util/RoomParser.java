package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.bootstrap.util.hotel.HotelCsvReader;
import org.microarchitecturovisco.hotelservice.bootstrap.util.room.RoomCapacityCalculator;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomDto;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class RoomParser {

    public void importRooms(String csvFilePath, List<HotelDto> hotelDtos) {
        Logger logger = Logger.getLogger("Bootstrap | Rooms");
        RoomCapacityCalculator capacityCalculator = new RoomCapacityCalculator();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                RoomDto roomDto = createNewRoom(logger, capacityCalculator, data, hotelDtos);

                // Add room to the corresponding hotel
                if (roomDto != null) {
                    Optional<HotelDto> hotelOpt = hotelDtos.stream()
                            .filter(hotel -> hotel.getHotelId() == roomDto.getHotelId())
                            .findFirst();
                    hotelOpt.ifPresent(hotel -> hotel.getRooms().add(roomDto));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RoomDto createNewRoom(Logger logger, RoomCapacityCalculator capacityCalculator, String[] data, List<HotelDto> hotelDtos) throws FileNotFoundException {
        int hotelId = Integer.parseInt(data[0]);
        String roomName = data[1];
        String description = data[2];
        int guestCapacity = capacityCalculator.calculateGuestCapacity(roomName);
        float pricePerAdult = Float.parseFloat(data[3]);

        Optional<HotelDto> hotelOpt = searchForHotel(hotelDtos, hotelId);

        if (hotelOpt.isPresent()) {
            return RoomDto.builder()
                    .roomId(UUID.randomUUID())
                    .hotelId(hotelOpt.get().getHotelId())
                    .name(roomName)
                    .description(description)
                    .guestCapacity(guestCapacity)
                    .pricePerAdult(pricePerAdult)
                    .build();
        } else {
            logger.info("Hotel not found for room with ID: " + hotelId);
            return null;
        }
    }

    private static Optional<HotelDto> searchForHotel(List<HotelDto> hotelDtos, int hotelId) throws FileNotFoundException {
        HotelCsvReader hotelCsvReader = new HotelCsvReader();
        // Retrieve hotel name from hotels.csv based on hotelId
        String hotelName = HotelCsvReader.getHotelNameById(hotelId);

        // Check if the hotel exists in the provided list
        Optional<HotelDto> hotelOpt = hotelDtos.stream()
                .filter(hotel -> hotel.getName().equals(hotelName))
                .findFirst();
        return hotelOpt;
    }

}
