package org.microarchitecturovisco.transport.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.microarchitecturovisco.transport.model.events.TransportCreatedEvent;
import org.microarchitecturovisco.transport.model.events.TransportEvent;
import org.microarchitecturovisco.transport.model.events.TransportReservationCreatedEvent;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.microarchitecturovisco.transport.repositories.TransportReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportEventSourcingHandler {

    private final TransportEventStore eventStore;

    private final TransportRepository transportRepository;
    private final TransportReservationRepository transportReservationRepository;

    public void project(UUID idTransport) {
        List<TransportEvent> events = eventStore.findTransportEventsByIdTransport(idTransport);

        Transport transport = new Transport();

        for (TransportEvent event : events) {
            if (event instanceof TransportCreatedEvent) {
                // TODO
            }
            if (event instanceof TransportReservationCreatedEvent) {
                apply((TransportReservationCreatedEvent) event, transport);
            }
        }
    }

    private void apply(TransportReservationCreatedEvent event, Transport transport) {
        TransportReservation transportReservation = TransportReservation.builder()
                .id(event.getIdTransportReservation())
                .numberOfSeats(event.getNumberOfSeats())
                .transport(transport)
                .build();
        transport.getTransportReservations().add(transportReservation);

        transportReservationRepository.save(transportReservation);
        transportRepository.save(transport);
    }
}
