package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.microarchitecturovisco.transport.model.events.TransportCreatedEvent;
import org.microarchitecturovisco.transport.model.events.TransportEvent;
import org.microarchitecturovisco.transport.model.events.TransportReservationCreatedEvent;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportEventSourcingHandler {

    private final TransportEventStore eventStore;

    private final TransportRepository transportRepository;
    private final TransportReservationRepository transportReservationRepository;
    private final TransportCourseRepository transportCourseRepository;
    private final LocationRepository locationRepository;

    public void project(UUID idTransport) {
        List<TransportEvent> transportEvents = eventStore.findTransportEventsByIdTransport(idTransport);

        Transport transport = new Transport();

        for (TransportEvent transportEvent : transportEvents) {
            if (transportEvent instanceof TransportCreatedEvent) {
                apply((TransportCreatedEvent) transportEvent, transport);
            }
            if (transportEvent instanceof TransportReservationCreatedEvent) {
                apply((TransportReservationCreatedEvent) transportEvent, transport);
            }
        }

        transportRepository.save(transport);
    }

    private void apply(TransportCreatedEvent event, Transport transport) {
        Location departureFrom = Location.builder()
                .id(event.getIdDepartureLocation())
                .country(event.getDepartureLocationCountry())
                .region(event.getDepartureLocationRegion())
                .build();

        Location arrivalAt = Location.builder()
                .id(event.getIdArrivalLocation())
                .country(event.getArrivalLocationCountry())
                .region(event.getArrivalLocationRegion())
                .build();
        locationRepository.saveAll(List.of(departureFrom, arrivalAt));

        TransportCourse course = TransportCourse.builder()
                .id(event.getIdTransportCourse())
                .departureFrom(departureFrom)
                .arrivalAt(arrivalAt)
                .type(event.getType())
                .build();
        transportCourseRepository.save(course);

        transport.setId(event.getIdTransport());
        transport.setDepartureDate(event.getDepartureDate());
        transport.setCourse(course);
        transport.setCapacity(event.getCapacity());
        transport.setPricePerAdult(event.getPricePerAdult());
        transport.setTransportReservations(new ArrayList<>());
    }

    private void apply(TransportReservationCreatedEvent event, Transport transport) {
        TransportReservation transportReservation = TransportReservation.builder()
                .id(event.getIdTransportReservation())
                .numberOfSeats(event.getNumberOfSeats())
                .transport(transport)
                .build();
        transport.getTransportReservations().add(transportReservation);

        transportReservationRepository.save(transportReservation);
    }
}
