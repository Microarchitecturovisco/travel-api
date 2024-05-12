package org.microarchitecturovisco.transport.bootstrap.util.parsers;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TransportParser {

    @Autowired
    private TransportCourseRepository transportCourseRepository;

    @Autowired
    private TransportRepository transportRepository;

    public void importTransports(String dataDirectory) {
        try (BufferedReader br = new BufferedReader(new FileReader(dataDirectory))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Transport transport = createNewTransport(data);
                transportRepository.save(transport);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Transport createNewTransport(String[] data) {
        int courseID = Integer.parseInt(data[0]);
        LocalDateTime departureDate = LocalDateTime.parse(data[1]);
        int capacity = Integer.parseInt(data[2]);
        float pricePerAdult = Float.parseFloat(data[3]);

        Optional<TransportCourse> courseOptional = transportCourseRepository.findById(courseID);
        TransportCourse course = courseOptional.orElseThrow(() -> new IllegalArgumentException("Transport course not found with ID: " + courseID));

        return new Transport(course, departureDate, capacity, pricePerAdult);
    }
}
