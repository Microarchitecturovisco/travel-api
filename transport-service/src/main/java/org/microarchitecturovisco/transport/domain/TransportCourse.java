package org.microarchitecturovisco.transport.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TransportCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Location departureFrom;

    @OneToOne
    private Location arrivalAt;

    public TransportCourse() {
    }

    public TransportCourse(Location departureFrom, Location arrivalAt) {
        this.departureFrom = departureFrom;
        this.arrivalAt = arrivalAt;
    }
}