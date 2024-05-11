package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportReservationRepository extends JpaRepository<Transport, Integer> {
}
