package org.microarchitecturovisco.hotelservice.model.cqrs.commands;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeleteRoomReservationCommand {
    private LocalDateTime commandTimeStamp;
    private UUID reservationId;
    private UUID roomId;
    private UUID hotelId;
}
