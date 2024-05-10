package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
}
