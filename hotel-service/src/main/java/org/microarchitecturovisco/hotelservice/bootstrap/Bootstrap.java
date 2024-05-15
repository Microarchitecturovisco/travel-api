package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.bootstrap.util.CateringOptionParser;
import org.microarchitecturovisco.hotelservice.bootstrap.util.HotelParser;
import org.microarchitecturovisco.hotelservice.bootstrap.util.LocationParser;
import org.microarchitecturovisco.hotelservice.bootstrap.util.RoomReservationParser;
import org.microarchitecturovisco.hotelservice.model.dto.CateringOptionDto;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final LocationParser locationParser;
    private final HotelParser hotelParser;
    private final CateringOptionParser cateringOptionParser;
    private final RoomReservationParser roomReservationParser;

    public File loadCSVInitFiles(String filepathInResources)
            throws FileNotFoundException {
        return ResourceUtils.getFile(
                filepathInResources);
    }


    @Override
    public void run(String... args) throws IOException {
        Logger logger = Logger.getLogger("Bootstrap");

        File hotelCsvFile = loadCSVInitFiles("classpath:initData/hotels.csv");
        File hotelDepartureOptionsCsvFile = loadCSVInitFiles("classpath:initData/hotel_departure_options.csv");
        File hotelPhotosCsvFile = loadCSVInitFiles("classpath:initData/hotel_photos.csv");
        File hotelRoomsCsvFile = loadCSVInitFiles("classpath:initData/hotel_rooms.csv");
        File hotelCateringOptionsCsvFile = loadCSVInitFiles("classpath:initData/hotel_food_options.csv");

        List<LocationDto> hotelLocations = locationParser.importLocations(hotelCsvFile.getPath());

        List<HotelDto> hotels = hotelParser.importHotels(hotelCsvFile.getPath(), hotelPhotosCsvFile.getPath(), hotelLocations);

        List<CateringOptionDto> cateringOptions = cateringOptionParser.importCateringOptions(hotelCateringOptionsCsvFile.getPath(), hotels);

        List<RoomReservationDto> roomReservations = roomReservationParser.importRoomReservations(hotels);

    }
}
