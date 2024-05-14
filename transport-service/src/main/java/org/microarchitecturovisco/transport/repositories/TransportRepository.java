package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportRepository extends JpaRepository<Transport, UUID> {
}
