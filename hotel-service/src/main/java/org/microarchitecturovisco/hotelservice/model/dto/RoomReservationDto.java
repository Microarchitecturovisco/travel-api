package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomReservationDto {

    private UUID reservationId;

    private UUID roomId;

    private UUID hotelId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;
}
