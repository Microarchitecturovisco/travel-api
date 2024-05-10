package org.microarchitecturovisco.transport.model.cqrs.aggregates;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportCommand;
import org.microarchitecturovisco.transport.model.cqrs.commands.UpdateTransportCommand;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransportAggregate {

    private TransportRepository transportRepository;

    public Transport handleCreateTransportCommand(CreateTransportCommand command) {

        // TODO create transport logic
        return null;
    }

    public Transport handleUpdateTransportCommand(UpdateTransportCommand command) {
        // TODO update transport logic
        return null;
    }
}
