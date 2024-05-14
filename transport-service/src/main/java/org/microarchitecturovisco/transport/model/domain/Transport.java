package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private TransportCourse course;

    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private int capacity;

    @NotNull
    private float pricePerAdult;

    @OneToMany(mappedBy="transport")
    private List<TransportReservation> transportReservations;


    public Transport(TransportCourse course, LocalDateTime departureDate, int capacity, float pricePerAdult) {
        this.course = course;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.pricePerAdult = pricePerAdult;
    }
}
