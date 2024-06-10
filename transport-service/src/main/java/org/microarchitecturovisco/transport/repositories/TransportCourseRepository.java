package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportCourseRepository extends JpaRepository<TransportCourse, UUID> {
}
