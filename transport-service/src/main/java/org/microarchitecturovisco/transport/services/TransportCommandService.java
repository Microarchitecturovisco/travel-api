package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.events.TransportCreatedEvent;
import org.microarchitecturovisco.transport.model.events.TransportReservationCreatedEvent;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

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

        eventSourcingHandler.project(command.getTransportDto().getIdTransport());
    }

    public void createReservation(CreateTransportReservationCommand command) {
        TransportReservationCreatedEvent transportReservationCreatedEvent = TransportReservationCreatedEvent.builder()
                .id(command.getUuid())
                .eventTimeStamp(command.getCommandTimeStamp())
                .idTransportReservation(command.getTransportReservationDto().getIdTransportReservation())
                .idTransport(command.getTransportReservationDto().getIdTransport())
                .build();

        transportEventStore.save(transportReservationCreatedEvent);

        eventSourcingHandler.project(command.getTransportReservationDto().getIdTransport());
        Logger logger = Logger.getLogger("TransportCommandService");
        logger.info("Projecting transport reservation for id " + command.getTransportReservationDto().getIdTransport());
    }
}
