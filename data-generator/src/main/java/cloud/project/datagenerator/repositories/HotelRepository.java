package cloud.project.datagenerator.repositories;


import cloud.project.datagenerator.model.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}
