package org.microarchitecturovisco.transport.bootstrap.util.parsers;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.microarchitecturovisco.transport.repositories.TransportReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class TransportReservationParser {
    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private TransportReservationRepository transportReservationRepository;


    public void importTransportReservations(String dataDirectory) {
        try (BufferedReader br = new BufferedReader(new FileReader(dataDirectory))) {
            String line;
            br.readLine();  // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                TransportReservation reservation = createNewTransportReservation(data);
                transportReservationRepository.save(reservation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TransportReservation createNewTransportReservation(String[] data) {
        int transportId = Integer.parseInt(data[0]);
        int numberOfSeats = Integer.parseInt(data[1]);

        Optional<Transport> transportOptional = transportRepository.findById(transportId);
        Transport transport = transportOptional.orElseThrow(() -> new IllegalArgumentException("Transport not found with ID: " + transportId));

        // Check if the total number of reserved seats exceeds the capacity of the transport
        checkNumberOfAvailableSeats(transportId, numberOfSeats, transport);

        return new TransportReservation(transport, numberOfSeats);
    }

    private void checkNumberOfAvailableSeats(int transportId, int numberOfSeats, Transport transport) {
        List<TransportReservation> existingReservations = transportReservationRepository.findByTransport(transport);

        int totalReservedSeats = existingReservations.stream().mapToInt(TransportReservation::getNumberOfSeats).sum() + numberOfSeats;

        if (totalReservedSeats > transport.getCapacity()) {
            throw new IllegalArgumentException("Number of seats requested exceeds the capacity of the transport with ID: " + transportId);
        }
    }
}
