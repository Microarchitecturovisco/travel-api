package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
