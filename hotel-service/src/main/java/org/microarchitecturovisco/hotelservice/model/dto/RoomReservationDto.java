package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomReservationDto {

    private UUID reservationId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;
}
