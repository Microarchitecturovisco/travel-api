package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByCountryAndRegion(String country, String region);
}
