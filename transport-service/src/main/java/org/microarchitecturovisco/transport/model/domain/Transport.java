package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private TransportCourse course;

    // the day the transport takes place
    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private int capacity;

    @NotNull
    private float pricePerAdult;

    @OneToMany(mappedBy="transport", cascade = CascadeType.ALL)
    private List<TransportReservation> transportReservations;
}
