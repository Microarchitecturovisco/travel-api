package cloud.project.datagenerator.hotels.bootstrap.util;


import cloud.project.datagenerator.hotels.domain.Hotel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

@Component
public class HotelParser {

    public List<Hotel> importHotels(Resource hotelCsvFilePath) throws IOException {
        Logger logger = Logger.getLogger("HotelDataParser");

        List<Hotel> hotels = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader( hotelCsvFilePath.getInputStream() ))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Hotel hotel = createHotel(data);
                hotels.add(hotel);
            }
        } catch (IOException e) {
            logger.severe("Error reading hotel CSV file: " + e.getMessage());
            throw e;
        }

        return hotels;
    }

    private Hotel createHotel(String[] data){
        String name = data[1];
        UUID hotelIdAsUUID = UUID.fromString(data[6]);

        return Hotel.builder()
                .id(hotelIdAsUUID)
                .name(name)
                .rooms(new ArrayList<>())
                .build();
    }
}
