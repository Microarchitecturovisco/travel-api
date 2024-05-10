package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByCountryAndRegion(String country, String region);
}
