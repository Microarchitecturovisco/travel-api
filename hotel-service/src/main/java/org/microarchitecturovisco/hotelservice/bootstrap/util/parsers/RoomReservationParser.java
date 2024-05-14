package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.domain.Room;
import org.microarchitecturovisco.hotelservice.domain.RoomReservation;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Component
public class RoomReservationParser {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    public void importRoomReservations() {
        Logger logger = Logger.getLogger("HOTELS");
        LocalDateTime dateFrom = LocalDateTime.of(2024, Month.JUNE, 1, 0, 0);
        LocalDateTime dateTo = LocalDateTime.of(2024, Month.OCTOBER, 30, 23, 59);
        long randomSeed = 1234;
        int numberOfRoomReservations = 30;
        int[] durationOptions = {7, 10, 14}; // Duration options in days

        Random random = new Random(randomSeed);

        for (int i = 0; i < numberOfRoomReservations; i++) {
            LocalDateTime randomDateFrom = generateRandomDateTime(dateFrom, dateTo, random);
            logger.info(String.valueOf(randomDateFrom));

            int randomDurationIndex = random.nextInt(durationOptions.length);
            int randomDuration = durationOptions[randomDurationIndex];
            Duration duration = Duration.ofDays(randomDuration);

            LocalDateTime randomDateTo = randomDateFrom.plus(duration);

            int roomId = random.nextInt((int) roomRepository.count()) + 1;
            Optional<Room> optionalRoom = roomRepository.findById(roomId);
            Room room = optionalRoom.orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

            RoomReservation roomReservation = new RoomReservation(randomDateFrom, randomDateTo, room);
            roomReservationRepository.save(roomReservation);
        }
    }

    private LocalDateTime generateRandomDateTime(LocalDateTime min, LocalDateTime max, Random random) {
        long minSeconds = min.toEpochSecond(ZoneOffset.UTC);
        long maxSeconds = max.toEpochSecond(ZoneOffset.UTC);
        long randomSeconds = minSeconds + random.nextInt((int) (maxSeconds - minSeconds + 1));
        return LocalDateTime.ofEpochSecond(randomSeconds, 0, ZoneOffset.UTC);
    }

}
