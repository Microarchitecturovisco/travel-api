package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findFirstByRegion(String region);
    Location findFirstByRegionIgnoreCase(String region);
}
