package cloud.project.datagenerator.transports.repositories;

import cloud.project.datagenerator.transports.domain.TransportCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportCourseRepository extends JpaRepository<TransportCourse, UUID> {
}
