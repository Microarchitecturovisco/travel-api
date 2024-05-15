package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.hotel h " +
            "JOIN h.location l " +
            "WHERE l.id IN :arrivalLocationIds " +
            "AND r IN (SELECT DISTINCT rr.room FROM RoomReservation rr " +
            "            WHERE NOT EXISTS (SELECT 1 FROM RoomReservation r " +
            "                              WHERE r.room = rr.room " +
            "                              AND (:dateFrom BETWEEN r.dateFrom AND r.dateTo " +
            "                                   OR :dateTo BETWEEN r.dateFrom AND r.dateTo)))")
    List<Room> findAvailableRoomsByLocationAndDate(
            @Param("arrivalLocationIds") List<Integer> arrivalLocationIds,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);
}
