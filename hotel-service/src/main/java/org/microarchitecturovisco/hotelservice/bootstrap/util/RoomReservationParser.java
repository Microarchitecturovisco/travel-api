package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class RoomReservationParser {

    public List<RoomReservationDto> importRoomReservations(List<HotelDto> hotels) {
        Logger logger = Logger.getLogger("HOTELS");
        List<RoomReservationDto> roomReservations = new ArrayList<>();
        LocalDateTime dateFrom = LocalDateTime.of(2024, Month.JUNE, 1, 0, 0);
        LocalDateTime dateTo = LocalDateTime.of(2024, Month.OCTOBER, 30, 23, 59);
        long randomSeed = 1234;
        int numberOfRoomReservations = 30;
        int[] durationOptions = {7, 10, 14}; // Duration options in days

        Random random = new Random(randomSeed);

        for (int i = 0; i < numberOfRoomReservations; i++) {
            LocalDateTime randomDateFrom = generateRandomDateTime(dateFrom, dateTo, random);
            int randomDurationIndex = random.nextInt(durationOptions.length);
            int randomDuration = durationOptions[randomDurationIndex];
            Duration duration = Duration.ofDays(randomDuration);

            LocalDateTime randomDateTo = randomDateFrom.plus(duration);

            HotelDto hotel= selectRandomHotel(hotels);
            while (hotel.getRooms().isEmpty()) {
                hotel= selectRandomHotel(hotels);
            }
            UUID hotelId = hotel.getHotelId();
            UUID roomId = selectRandomRoom(hotel.getRooms()).getRoomId();
            RoomReservationDto roomReservation = new RoomReservationDto();
            roomReservation.setReservationId(UUID.randomUUID());
            roomReservation.setDateFrom(randomDateFrom);
            roomReservation.setDateTo(randomDateTo);
            roomReservation.setHotelId(hotelId);
            roomReservation.setRoomId(roomId);
            roomReservations.add(roomReservation);
        }

        return roomReservations;
    }

    private HotelDto selectRandomHotel(List<HotelDto> hotels) {
        Random random = new Random();
        int randomIndex = random.nextInt(hotels.size());
        return hotels.get(randomIndex);
    }

    private RoomDto selectRandomRoom(List<RoomDto> rooms) {
        Random random = new Random();
        int randomIndex = random.nextInt(rooms.size());
        return rooms.get(randomIndex);
    }

    private LocalDateTime generateRandomDateTime(LocalDateTime min, LocalDateTime max, Random random) {
        long minSeconds = min.toEpochSecond(ZoneOffset.UTC);
        long maxSeconds = max.toEpochSecond(ZoneOffset.UTC);
        long randomSeconds = minSeconds + random.nextInt((int) (maxSeconds - minSeconds + 1));
        return LocalDateTime.ofEpochSecond(randomSeconds, 0, ZoneOffset.UTC);
    }
}
