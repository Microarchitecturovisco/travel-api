package cloud.project.datagenerator.bootstrap.util.hotel;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HotelCsvReader {

    private final ResourceLoader resourceLoader;

    // Method to read hotels.csv and retrieve hotel name based on hotelId
    public String getHotelNameById(int hotelId) throws FileNotFoundException {
        Map<Integer, String> hotelIdToNameMap = readHotelCsvFile(resourceLoader.getResource("classpath:initData/hotels.csv"));
        return hotelIdToNameMap.getOrDefault(hotelId, "Hotel not found"); // Return hotel name or "Hotel not found" if not found
    }

    // Method to read hotels.csv and create a mapping of hotelId to hotelName
    private static Map<Integer, String> readHotelCsvFile(Resource resource) {
        Map<Integer, String> hotelIdToNameMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String hotelName = data[1];
                hotelIdToNameMap.put(hotelId, hotelName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hotelIdToNameMap;
    }
}
