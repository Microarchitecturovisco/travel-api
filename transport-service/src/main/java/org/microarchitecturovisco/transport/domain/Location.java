package org.microarchitecturovisco.transport.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

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

    @NotNull
    private String country;

    private String region;

    @OneToOne(mappedBy = "departureFrom")
    private TransportCourse transportCourseFrom;

    @OneToOne(mappedBy = "arrivalAt")
    private TransportCourse transportCourseAt;
}
