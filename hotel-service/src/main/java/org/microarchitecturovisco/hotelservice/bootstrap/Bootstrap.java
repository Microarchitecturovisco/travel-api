package org.microarchitecturovisco.hotelservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final HotelRepository hotelRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        List<Hotel> hotels = new ArrayList<>();

        logger.info("Saved hotels: " + hotels);
    }
}
