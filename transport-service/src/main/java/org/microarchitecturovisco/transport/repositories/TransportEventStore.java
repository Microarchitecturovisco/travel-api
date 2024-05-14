package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.events.TransportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransportEventStore extends JpaRepository<TransportEvent, UUID> {

    @Query("select t.idTransport from TransportEvent as t")
    List<UUID> getAllTransportIds();

    List<TransportEvent> findTransportEventsByIdTransport(UUID idTransport);
}
