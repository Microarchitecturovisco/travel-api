package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.events.TransportCreatedEvent;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportCommandService {

    private final TransportEventStore transportEventStore;

    public void createTransport(CreateTransportCommand command) {
        TransportCreatedEvent transportCreatedEvent = new TransportCreatedEvent(
                command.getUuid(), command.getCommandTimeStamp(), command.getTransportDto()
        );

        transportEventStore.save(transportCreatedEvent);
    }
}
