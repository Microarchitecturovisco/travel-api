package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.microarchitecturovisco.transport.model.events.*;
import org.microarchitecturovisco.transport.repositories.LocationRepository;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.microarchitecturovisco.transport.repositories.TransportReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportEventSourcingHandler {

    private final TransportRepository transportRepository;
    private final TransportReservationRepository transportReservationRepository;
    private final TransportCourseRepository transportCourseRepository;
    private final LocationRepository locationRepository;

    public void project(List<TransportEvent> transportEvents) {
        for (TransportEvent transportEvent : transportEvents) {
            if (transportEvent instanceof TransportCreatedEvent) {
                apply((TransportCreatedEvent) transportEvent);
            }
            if (transportEvent instanceof TransportReservationCreatedEvent) {
                apply((TransportReservationCreatedEvent) transportEvent);
            }
            if (transportEvent instanceof TransportReservationDeletedEvent) {
                apply((TransportReservationDeletedEvent) transportEvent);
            }
            if (transportEvent instanceof TransportUpdateEvent){
                apply((TransportUpdateEvent) transportEvent);
            }
            if (transportEvent instanceof TransportOnlyCreatedEvent){
                apply((TransportOnlyCreatedEvent) transportEvent);
            }
        }
    }

    private void apply(TransportCreatedEvent event) {
        Transport transport = new Transport();

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
        transportRepository.save(transport);
    }

    private void apply(TransportReservationCreatedEvent event) {
//        System.out.println("TransportReservationCreatedEvent: " + event.toString());

        Transport transport = transportRepository.findById(event.getIdTransport()).orElseThrow(RuntimeException::new);

        TransportReservation transportReservation = TransportReservation.builder()
                .id(event.getId())
                .numberOfSeats(event.getNumberOfSeats())
                .transport(transport)
                .mainReservationId(event.getReservationId())
                .build();
        transport.getTransportReservations().add(transportReservation);

        transportReservationRepository.save(transportReservation);
        transportRepository.save(transport);
    }

    private void apply(TransportReservationDeletedEvent event) {

        UUID transportId = event.getIdTransport();
        UUID reservationId = event.getReservationId();

        // Find the transport
        Transport transport = transportRepository.findById(transportId).orElse(null);
        if (transport != null) {
            // Delete reservations associated with the transport and given reservation ID
            List<TransportReservation> transportReservations = transport.getTransportReservations();
            for (TransportReservation reservation : transportReservations) {
                if (reservation.getMainReservationId().equals(reservationId)) {
                    transportReservationRepository.delete(reservation);
                }
            }
        }

    }

    private void apply(TransportUpdateEvent event)
    {
        Transport transport = transportRepository.findById(event.getIdTransport()).orElseThrow(RuntimeException::new);

        transport.setCapacity(event.getCapacity());
        transport.setPricePerAdult(event.getPricePerAdult());

        transportRepository.save(transport);
    }

    private void apply(TransportOnlyCreatedEvent event){
        TransportCourse transportCourse = transportCourseRepository.findById(event.getCourseId()).orElseThrow(RuntimeException::new);

        Transport transport = new Transport();
        transport.setId(event.getIdTransport());
        transport.setDepartureDate(event.getDepartureDate());
        transport.setCourse(transportCourse);
        transport.setCapacity(event.getCapacity());

        transportRepository.save(transport);
        transportCourseRepository.save(transportCourse);
    }
}
