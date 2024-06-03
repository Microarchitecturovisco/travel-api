package cloud.project.datagenerator.hotels.repositories;

import cloud.project.datagenerator.hotels.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId")
    List<Room> findAllRoomsByHotelId(@Param("hotelId") UUID hotelId);
}

