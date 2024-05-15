package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.bootstrap.util.catering.CateringPriceCalculator;
import org.microarchitecturovisco.hotelservice.bootstrap.util.catering.CateringTypeMapper;
import org.microarchitecturovisco.hotelservice.bootstrap.util.hotel.HotelCsvReader;
import org.microarchitecturovisco.hotelservice.model.domain.CateringType;
import org.microarchitecturovisco.hotelservice.model.dto.CateringOptionDto;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class CateringOptionParser {

    public List<CateringOptionDto> importCateringOptions(String csvFilePath, List<HotelDto> hotels) {
        Logger logger = Logger.getLogger("Bootstrap | CateringOptions");
        List<CateringOptionDto> cateringOptions = new ArrayList<>();
        CateringPriceCalculator cateringPriceCalculator = new CateringPriceCalculator();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                CateringOptionDto cateringOption = createNewCateringOption(logger, cateringPriceCalculator, data, hotels);
                if (cateringOption != null) {
                    cateringOptions.add(cateringOption);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cateringOptions;
    }

    private CateringOptionDto createNewCateringOption(Logger logger, CateringPriceCalculator cateringPriceCalculator, String[] data, List<HotelDto> hotels) throws FileNotFoundException {
        CateringTypeMapper cateringTypeMapper = new CateringTypeMapper();
        int hotelId = Integer.parseInt(data[0]);
        String foodOption = data[1];
        float rating = Float.parseFloat(data[2]);

        CateringType cateringType = cateringTypeMapper.mapToCateringType(foodOption);
        if (cateringType != null) {
            float price = cateringPriceCalculator.calculateCateringPrice(cateringType);
            CateringOptionDto cateringOption = new CateringOptionDto();
            cateringOption.setCateringId(UUID.randomUUID());
            cateringOption.setType(cateringType);
            cateringOption.setRating(rating);
            cateringOption.setPrice(price);

            Optional<HotelDto> hotelOpt = searchForHotel(hotels, hotelId);

            if (hotelOpt.isPresent()) {
                cateringOption.setHotelId(hotelOpt.get().getHotelId());
                return cateringOption;
            } else {
                logger.info("Hotel not found for catering option with ID: " + hotelId);
            }
        } else {
            logger.info("Failed to map food option: " + foodOption);
        }
        return null;
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
