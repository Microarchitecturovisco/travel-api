package org.microarchitecturovisco.transport.model.cqrs.commands;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeleteTransportReservationCommand {
    private UUID reservationId;
    private UUID transportReservationsId;
    private LocalDateTime commandTimeStamp;
}
