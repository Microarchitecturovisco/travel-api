package org.microarchitecturovisco.transport.model.cqrs.commands;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportReservationDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateTransportReservationCommand {
    private UUID uuid;
    private LocalDateTime commandTimeStamp;

    private TransportReservationDto transportReservationDto;
}
