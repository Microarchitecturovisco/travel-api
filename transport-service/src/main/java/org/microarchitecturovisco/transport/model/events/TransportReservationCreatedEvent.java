package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransportReservationCreatedEvent extends TransportEvent {

    private UUID reservationId;
    private int numberOfSeats;

}
