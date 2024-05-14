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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name="transport_id", nullable=false)
    private Transport transport;

    @NotNull
    private int numberOfSeats;

    public TransportReservation(Transport transport, int numberOfSeats) {
        this.transport = transport;
        this.numberOfSeats = numberOfSeats;
    }
}
