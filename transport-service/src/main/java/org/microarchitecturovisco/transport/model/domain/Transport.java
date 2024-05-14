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
    private UUID id;

    @ManyToOne
    private TransportCourse course;

    // the day the transport takes place
    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private int capacity;

    @NotNull
    private float pricePerAdult;

    @OneToMany(mappedBy="transport", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TransportReservation> transportReservations;
}
