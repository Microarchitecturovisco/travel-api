package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.bootstrap.util.parsers.room.RoomCapacityCalculator;
import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class RoomParser {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;


    public void importRooms(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | Rooms");
        RoomCapacityCalculator capacityCalculator = new RoomCapacityCalculator();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Room room = createNewRoom(logger, capacityCalculator, data);

                roomRepository.save(room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Room createNewRoom(Logger logger, RoomCapacityCalculator capacityCalculator, String[] data) {
        int hotelId = Integer.parseInt(data[0]);
        String roomName = data[1];
        String description = data[2];
        int guestCapacity = capacityCalculator.calculateGuestCapacity(roomName);
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
        return room;
    }
}
