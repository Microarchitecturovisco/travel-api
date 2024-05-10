package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    // You can define custom query methods here if needed
}
