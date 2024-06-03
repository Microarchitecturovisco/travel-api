package cloud.project.datagenerator.transports.repositories;

import cloud.project.datagenerator.transports.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Location findFirstByRegion(String region);
    Location findFirstByRegionIgnoreCase(String region);
}
