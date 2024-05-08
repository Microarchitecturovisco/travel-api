package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "transport_course_from_id", nullable = false)
    private Location departureFrom;

    @ManyToOne
    @JoinColumn(name = "transport_course_at_id", nullable = false)
    private Location arrivalAt;

}