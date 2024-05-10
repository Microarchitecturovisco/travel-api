package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.domain.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomReservationRepository extends JpaRepository<RoomReservation, Integer> {
}
