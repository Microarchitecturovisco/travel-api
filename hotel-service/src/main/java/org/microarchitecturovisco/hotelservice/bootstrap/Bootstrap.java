package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;

import org.microarchitecturovisco.hotelservice.bootstrap.util.*;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateHotelCommand;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.*;
import org.microarchitecturovisco.hotelservice.services.HotelsCommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final LocationParser locationParser;
    private final HotelParser hotelParser;
    private final CateringOptionParser cateringOptionParser;
    private final RoomReservationParser roomReservationParser;
    private final HotelsCommandService hotelsCommandService;
    private final RoomParser roomParser;
    private final ResourceLoader resourceLoader;

    public File loadCSVInitFiles(String filepathInResources)
            throws FileNotFoundException {
        return ResourceUtils.getFile(
                filepathInResources);
    }


    @Override
    public void run(String... args) throws IOException {
        Logger logger = Logger.getLogger("Bootstrap");

        Resource hotelCsvFile = resourceLoader.getResource("classpath:initData/hotels.csv");
        Resource hotelPhotosCsvFile = resourceLoader.getResource("classpath:initData/hotel_photos.csv");
        Resource hotelRoomsCsvFile = resourceLoader.getResource("classpath:initData/hotel_rooms.csv");
        Resource hotelCateringOptionsCsvFile = resourceLoader.getResource("classpath:initData/hotel_food_options.csv");

        List<LocationDto> hotelLocations = locationParser.importLocations(hotelCsvFile);
        List<HotelDto> hotels = hotelParser.importHotels(hotelCsvFile, hotelPhotosCsvFile, hotelLocations);
        List<CateringOptionDto> cateringOptions = cateringOptionParser.importCateringOptions(hotelCateringOptionsCsvFile, hotels);
        roomParser.importRooms(hotelRoomsCsvFile, hotels);
        List<RoomReservationDto> roomReservations = roomReservationParser.importRoomReservations(hotels);

        for (HotelDto hotelDto : hotels){
            hotelsCommandService.createHotel(CreateHotelCommand.builder()
                    .uuid(hotelDto.getHotelId())
                    .commandTimeStamp(LocalDateTime.now())
                    .hotelDto(hotelDto)
                    .build());
        }
        for (RoomReservationDto roomReservation : roomReservations){
            hotelsCommandService.createReservation(CreateRoomReservationCommand.builder()
                    .hotelId(roomReservation.getHotelId())
                    .roomId(roomReservation.getRoomId())
                    .roomReservationDto(roomReservation)
                    .commandTimeStamp(LocalDateTime.now())
                    .build()
            );
        }
    }
}
