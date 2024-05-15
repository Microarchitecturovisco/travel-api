package org.microarchitecturovisco.hotelservice.bootstrap.util.hotel;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HotelCsvReader {

    public static File loadCSVInitFiles(String filepathInResources) throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:" + filepathInResources);
    }

    // Method to read hotels.csv and retrieve hotel name based on hotelId
    public static String getHotelNameById(int hotelId) throws FileNotFoundException {
        File hotelCsvFile = loadCSVInitFiles("initData/hotels.csv");
        Map<Integer, String> hotelIdToNameMap = readHotelCsvFile(hotelCsvFile);
        return hotelIdToNameMap.getOrDefault(hotelId, "Hotel not found"); // Return hotel name or "Hotel not found" if not found
    }

    // Method to read hotels.csv and create a mapping of hotelId to hotelName
    private static Map<Integer, String> readHotelCsvFile(File hotelCsvFile) {
        Map<Integer, String> hotelIdToNameMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(hotelCsvFile))) {
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
