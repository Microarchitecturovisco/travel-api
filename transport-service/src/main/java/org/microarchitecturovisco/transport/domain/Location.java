package org.microarchitecturovisco.transport.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String country;

    private String region;

    @OneToOne(mappedBy = "departureFrom")
    private TransportCourse transportCourseFrom;

    @OneToOne(mappedBy = "arrivalAt")
    private TransportCourse transportCourseAt;
}
