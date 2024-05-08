package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transport_courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private TransportType type;

    @ManyToOne
    @JoinColumn(name = "transport_course_from_location_id", nullable = false)
    private Location departureFrom;

    @ManyToOne
    @JoinColumn(name = "transport_course_at_location_id", nullable = false)
    private Location arrivalAt;

}