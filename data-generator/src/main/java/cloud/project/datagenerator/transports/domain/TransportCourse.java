package cloud.project.datagenerator.transports.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transport_courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCourse {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransportType type;

    @ManyToOne
    @JoinColumn(name = "transport_course_from_location_id", nullable = false)
    private Location departureFrom;

    @ManyToOne
    @JoinColumn(name = "transport_course_at_location_id", nullable = false)
    private Location arrivalAt;

}