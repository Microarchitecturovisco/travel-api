package org.microarchitecturovisco.reservationservice.domain.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Builder
@Getter
@Setter
public class ReservationUpdateEvent extends ReservationEvent {
    private UUID idReservation;
    private Boolean paid;
}
