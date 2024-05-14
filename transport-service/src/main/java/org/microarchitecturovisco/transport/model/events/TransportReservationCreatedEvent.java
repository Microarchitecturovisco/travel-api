package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transports_reservation_created_events")
@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class TransportReservationCreatedEvent extends TransportEvent {

    private int idTransportReservation;
    private int numberOfSeats;

}
