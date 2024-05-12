package org.microarchitecturovisco.hotelservice.bootstrap.util.parsers;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class PhotosParser {

    public Map<Integer, List<String>> hotelPhotosMap;


    public void importPhotos(String csvFilePath) {
        Map<Integer, List<String>> hotelPhotosMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                int hotelId = Integer.parseInt(data[0]);
                String photoUrl = data[1];

                hotelPhotosMap.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(photoUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hotelPhotosMap = hotelPhotosMap;
    }
}
