package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.events.RoomEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomEventStore extends JpaRepository<RoomEvent, UUID> {

    List<RoomEvent> findRoomEventsById(UUID idRoom);
}
