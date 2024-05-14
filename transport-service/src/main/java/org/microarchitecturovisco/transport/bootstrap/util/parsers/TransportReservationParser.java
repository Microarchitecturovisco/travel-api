package org.microarchitecturovisco.transport.bootstrap.util.parsers;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.microarchitecturovisco.transport.repositories.TransportReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class TransportReservationParser {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private TransportReservationRepository transportReservationRepository;

    public void importTransportReservations() {
        int numberOfReservations = 30;
        long randomSeed = 1234;

        Random random = new Random(randomSeed);

        List<Transport> transports = transportRepository.findAll();

        for (int i = 0; i < numberOfReservations; i++) {
            Transport randomTransport = transports.get(random.nextInt(transports.size()));

            int numberOfSeats = random.nextInt(randomTransport.getCapacity()) + 1;

            TransportReservation transportReservation = new TransportReservation(randomTransport, numberOfSeats);
            transportReservationRepository.save(transportReservation);
        }
    }
}
