package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.events.TransportEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransportEventStore extends JpaRepository<TransportEvent, Integer> {
    List<TransportEvent> findTransportEventsByIdTransport(UUID idTransport);
}
