package org.microarchitecturovisco.reservationservice.domain.events;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateEvent extends ReservationEvent {
    private UUID idReservation;
    private Boolean paid;
}
