package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.DeleteTransportReservationCommand;
import org.microarchitecturovisco.transport.model.events.TransportCreatedEvent;
import org.microarchitecturovisco.transport.model.events.TransportReservationCreatedEvent;
import org.microarchitecturovisco.transport.model.events.TransportReservationDeletedEvent;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportCommandService {

    private final TransportEventStore transportEventStore;

    private final TransportEventSourcingHandler eventSourcingHandler;

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
                .idTransportReservation(command.getTransportReservationDto().getIdTransportReservation())
                .numberOfSeats(command.getTransportReservationDto().getNumberOfSeats())
                .idTransport(command.getTransportReservationDto().getIdTransport())
                .build();

        transportEventStore.save(transportReservationCreatedEvent);
        eventSourcingHandler.project(List.of(transportReservationCreatedEvent));
    }

    public void deleteReservation(DeleteTransportReservationCommand command) {
        TransportReservationDeletedEvent reservationDeletedEvent =  TransportReservationDeletedEvent.builder()
                .eventTimeStamp(command.getCommandTimeStamp())
                .reservationId(command.getReservationId())
                .transportId(command.getTransportReservationsId())
                .build();

        reservationDeletedEvent.setId(UUID.randomUUID());

        transportEventStore.save(reservationDeletedEvent);
        eventSourcingHandler.project(List.of(reservationDeletedEvent));
    }

}
