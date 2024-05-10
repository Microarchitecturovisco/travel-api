package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.bootstrap.util.CsvParser;
import org.microarchitecturovisco.hotelservice.domain.Location;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final CsvParser csvParser;
    private final HotelRepository hotelRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        // Path to the CSV file
        String csvFilePath = "hotel-service\\src\\main\\java\\org\\microarchitecturovisco\\hotelservice\\bootstrap\\data\\hotels.csv";

        // Parse hotels from CSV
        List<Location> locations = csvParser.createLocations(csvFilePath);
        
        logger.info("Saved locations: " + locations);
    }
}
