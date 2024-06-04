package cloud.project.datagenerator.hotels.repositories;


import cloud.project.datagenerator.hotels.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}
