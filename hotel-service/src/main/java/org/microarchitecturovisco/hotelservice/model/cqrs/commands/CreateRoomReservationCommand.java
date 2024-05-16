package org.microarchitecturovisco.hotelservice.model.cqrs.commands;
import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.microarchitecturovisco.hotelservice.model.dto.RoomReservationDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateRoomReservationCommand {
    private LocalDateTime commandTimeStamp;

    private RoomReservationDto roomReservationDto;
    private UUID roomId;
    private UUID hotelId;
}
