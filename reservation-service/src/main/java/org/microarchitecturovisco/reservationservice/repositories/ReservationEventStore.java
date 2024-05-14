package org.microarchitecturovisco.reservationservice.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationEventStore extends JpaRepository<ReservationEvent, Integer> {
}
