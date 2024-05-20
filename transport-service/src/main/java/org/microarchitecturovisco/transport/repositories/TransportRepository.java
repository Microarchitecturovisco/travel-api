package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransportRepository extends JpaRepository<Transport, UUID> {
    List<Transport> findByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(
            LocalDateTime dateFrom, LocalDateTime dateTo);
}
