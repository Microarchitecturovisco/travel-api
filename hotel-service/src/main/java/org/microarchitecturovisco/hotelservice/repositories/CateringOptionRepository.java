package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.domain.CateringOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CateringOptionRepository extends JpaRepository<CateringOption, Integer> {
}
