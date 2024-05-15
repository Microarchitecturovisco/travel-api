package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class RoomReservationDto {

    private UUID reservationId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private RoomDto room;

    public RoomReservationDto(){}
}
