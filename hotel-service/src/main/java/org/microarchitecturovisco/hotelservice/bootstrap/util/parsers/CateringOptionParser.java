package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.microarchitecturovisco.hotelservice.bootstrap.util.parsers.catering.CateringPriceCalculator;
import org.microarchitecturovisco.hotelservice.bootstrap.util.parsers.catering.CateringTypeMapper;
import org.microarchitecturovisco.hotelservice.domain.*;
import org.microarchitecturovisco.hotelservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CateringOptionParser {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CateringOptionRepository cateringOptionRepository;

    public void importCateringOptions(String csvFilePath) {
        Logger logger = Logger.getLogger("Bootstrap | CateringOptions");
        CateringPriceCalculator cateringPriceCalculator = new CateringPriceCalculator();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                CateringOption cateringOption = createNewCateringOption(logger, cateringPriceCalculator, data);
                cateringOptionRepository.save(cateringOption);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CateringOption createNewCateringOption(Logger logger, CateringPriceCalculator cateringPriceCalculator, String[] data) {
        CateringTypeMapper cateringTypeMapper = new CateringTypeMapper();
        int hotelId = Integer.parseInt(data[0]);
        String foodOption = data[1];
        float rating = Float.parseFloat(data[2]);

        CateringType cateringType = cateringTypeMapper.mapToCateringType(foodOption);
        if (cateringType != null) {
            float price = cateringPriceCalculator.calculateCateringPrice(cateringType);
            CateringOption cateringOption = CateringOption.builder()
                    .type(cateringType)
                    .price(price)
                    .rating(rating)
                    .build();

            Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
            if (hotel != null) {
                cateringOption.setHotel(hotel);
            } else {
                logger.info("Hotel not found for catering option with ID: " + hotelId);
            }
            return cateringOption;
        } else {
            logger.info("Failed to map food option: " + foodOption);
        }
        return new CateringOption();
    }
}
