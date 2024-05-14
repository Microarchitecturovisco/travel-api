package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportReservationRepository extends JpaRepository<TransportReservation, Integer> {
}
