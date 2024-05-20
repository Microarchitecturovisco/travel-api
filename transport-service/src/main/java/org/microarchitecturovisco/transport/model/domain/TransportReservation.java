package org.microarchitecturovisco.transport.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transports_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportReservation {
    @Id
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="transport_id", nullable=false)
    private Transport transport;

    @NotNull
    private int numberOfSeats;

    private UUID mainReservationId;
}
