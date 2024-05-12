package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.domain.Room;
import org.microarchitecturovisco.hotelservice.domain.RoomReservation;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class RoomReservationParser {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    public void importRoomReservations(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                RoomReservation roomReservation = createNewRoomReservation(data);
                roomReservationRepository.save(roomReservation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RoomReservation createNewRoomReservation(String[] data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        int roomId = Integer.parseInt(data[0]);
        LocalDateTime dateFrom = LocalDateTime.parse(data[1], formatter);
        LocalDateTime dateTo = LocalDateTime.parse(data[2], formatter);

        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Room room = optionalRoom.orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        return new RoomReservation(dateFrom, dateTo, room);
    }
}
