package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByCountryAndRegion(String country, String region);
}
