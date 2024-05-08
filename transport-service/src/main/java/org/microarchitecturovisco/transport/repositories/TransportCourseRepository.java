package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportCourseRepository extends JpaRepository<TransportCourse, Integer> {
}
