package org.microarchitecturovisco.reservationservice.domain.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReservationUpdateEvent extends ReservationEvent {
    private Integer id;
    private Boolean paid;
}
