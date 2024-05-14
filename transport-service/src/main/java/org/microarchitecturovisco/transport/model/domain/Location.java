package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    private UUID id;

    @NotNull
    private String country;

    private String region;

    @OneToMany(mappedBy = "departureFrom")
    private List<TransportCourse> transportCourseFrom;

    @OneToMany(mappedBy = "arrivalAt")
    private List<TransportCourse> transportCourseAt;

    public Location(String country, String region) {
        this.id = null;
        this.country = country;
        this.region = region;
    }
}
