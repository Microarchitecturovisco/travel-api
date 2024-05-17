package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.hotel h " +
            "JOIN h.location l " +
            "LEFT JOIN RoomReservation rr ON r = rr.room AND (:dateFrom BETWEEN rr.dateFrom AND rr.dateTo OR :dateTo BETWEEN rr.dateFrom AND rr.dateTo) " +
            "WHERE l.id IN :arrivalLocationIds " +
            "AND (rr IS NULL OR NOT EXISTS (SELECT 1 FROM RoomReservation rrv " +
            "                               WHERE rrv.room = rr.room " +
            "                               AND (:dateFrom BETWEEN rrv.dateFrom AND rrv.dateTo " +
            "                                    OR :dateTo BETWEEN rrv.dateFrom AND rrv.dateTo)))")
    List<Room> findAvailableRoomsByLocationAndDate(
            @Param("arrivalLocationIds") List<UUID> arrivalLocationIds,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);

    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.hotel h " +
            "LEFT JOIN RoomReservation rr ON r = rr.room AND (:dateFrom BETWEEN rr.dateFrom AND rr.dateTo OR :dateTo BETWEEN rr.dateFrom AND rr.dateTo) " +
            "WHERE h.id = :hotelId " +
            "AND (rr IS NULL OR NOT EXISTS (SELECT 1 FROM RoomReservation rrv " +
            "                               WHERE rrv.room = rr.room " +
            "                               AND (:dateFrom BETWEEN rrv.dateFrom AND rrv.dateTo " +
            "                                    OR :dateTo BETWEEN rrv.dateFrom AND rrv.dateTo)))")
    List<Room> findAvailableRoomsByHotelAndDate(
            @Param("hotelId") UUID hotelId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId")
    List<Room> findAllRoomsByHotelId(@Param("hotelId") UUID hotelId);
}
