package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    @Query("SELECT DISTINCT h FROM Hotel h JOIN h.location l WHERE l.id IN :arrivalLocationIds")
    List<Hotel> findHotelsByArrivalLocationIds(@Param("arrivalLocationIds") List<Integer> arrivalLocationIds);

}
