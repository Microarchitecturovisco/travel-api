package org.microarchitecturovisco.transport.domain;

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

    @OneToOne
    @JoinColumn(name = "location_from_id")
    private Location departureFrom;

    @OneToOne
    @JoinColumn(name = "location_to_id")
    private Location arrivalAt;

}