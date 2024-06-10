package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.DeleteTransportReservationCommand;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.events.*;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportCommandService {

    private final TransportEventStore transportEventStore;

    private final TransportEventSourcingHandler eventSourcingHandler;
    private final TransportCourseRepository transportCourseRepository;

    public void createTransport(CreateTransportCommand command) {
        TransportCreatedEvent transportCreatedEvent = new TransportCreatedEvent(
                command.getUuid(), command.getCommandTimeStamp(), command.getTransportDto()
        );

        transportEventStore.save(transportCreatedEvent);
        eventSourcingHandler.project(List.of(transportCreatedEvent));
    }

    public void createReservation(CreateTransportReservationCommand command) {
        TransportReservationCreatedEvent transportReservationCreatedEvent = TransportReservationCreatedEvent.builder()
                .id(command.getUuid())
                .eventTimeStamp(command.getCommandTimeStamp())
                .reservationId(command.getTransportReservationDto().getReservationId())
                .numberOfSeats(command.getTransportReservationDto().getNumberOfSeats())
                .idTransport(command.getTransportReservationDto().getIdTransport())
                .build();


        transportEventStore.save(transportReservationCreatedEvent);
        eventSourcingHandler.project(List.of(transportReservationCreatedEvent));
    }

    public void deleteReservation(DeleteTransportReservationCommand command) {
        TransportReservationDeletedEvent reservationDeletedEvent =  TransportReservationDeletedEvent.builder()
                .id(UUID.randomUUID())
                .eventTimeStamp(command.getCommandTimeStamp())
                .reservationId(command.getReservationId())
                .idTransport(command.getTransportId())
                .build();

        System.out.println("TransportReservationDeletedEvent: " + reservationDeletedEvent);

        transportEventStore.save(reservationDeletedEvent);
        eventSourcingHandler.project(List.of(reservationDeletedEvent));
    }

    public void updateTransport(UUID transportId, int capacity, float pricePerAdult) {
        TransportUpdateEvent transportUpdateEvent =  new TransportUpdateEvent(transportId, capacity, pricePerAdult);
        transportEventStore.save(transportUpdateEvent);
        eventSourcingHandler.project(List.of(transportUpdateEvent));
    }

    public void createTransport(UUID transportId, UUID courseId, LocalDateTime departureDate, int capacity,
                                float pricePerAdult) {
        TransportCourse transportCourse = transportCourseRepository.findById(courseId).orElse(null);
        if (transportCourse == null) return;

        TransportCreatedEvent transportCreatedEvent = TransportCreatedEvent.builder()
                .idTransport(transportId)
                .idTransportCourse(courseId)
                .departureDate(departureDate)
                .eventTimeStamp(LocalDateTime.now())
                .capacity(capacity)
                .pricePerAdult(pricePerAdult)
                .idDepartureLocation(transportCourse.getDepartureFrom().getId())
                .departureLocationCountry(transportCourse.getDepartureFrom().getCountry())
                .departureLocationRegion(transportCourse.getDepartureFrom().getRegion())
                .arrivalLocationCountry(transportCourse.getArrivalAt().getCountry())
                .arrivalLocationRegion(transportCourse.getArrivalAt().getRegion())
                .idArrivalLocation(transportCourse.getArrivalAt().getId())
                .type(transportCourse.getType())
                .build();
        transportEventStore.save(transportCreatedEvent);
        eventSourcingHandler.project(List.of(transportCreatedEvent));
    }

}
