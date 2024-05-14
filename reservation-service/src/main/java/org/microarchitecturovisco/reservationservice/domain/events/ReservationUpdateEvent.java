package org.microarchitecturovisco.reservationservice.domain.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Builder
@Getter
@Setter
public class ReservationUpdateEvent extends ReservationEvent {
    private String idReservation;
    private Boolean paid;
}
