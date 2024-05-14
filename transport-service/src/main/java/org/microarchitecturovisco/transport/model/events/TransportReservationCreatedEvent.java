package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "transports_reservation_created_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransportReservationCreatedEvent extends TransportEvent {

    private UUID idTransportReservation;
    private int numberOfSeats;

}
