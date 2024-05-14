package org.microarchitecturovisco.reservationservice.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateReservationCommand {
    Integer reservationId;
    boolean paid;
}
