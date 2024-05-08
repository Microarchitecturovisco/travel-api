package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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

    @OneToMany(mappedBy = "departureFrom")
    private List<TransportCourse> transportCourseFrom;

    @OneToMany(mappedBy = "arrivalAt")
    private List<TransportCourse> transportCourseAt;
}
