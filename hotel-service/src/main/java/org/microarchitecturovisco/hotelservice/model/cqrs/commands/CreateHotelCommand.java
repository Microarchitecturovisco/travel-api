package org.microarchitecturovisco.hotelservice.model.cqrs.commands;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateHotelCommand {
    private UUID uuid;
    private LocalDateTime commandTimeStamp;

    private HotelDto hotelDto;
}
