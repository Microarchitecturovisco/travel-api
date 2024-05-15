package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.microarchitecturovisco.hotelservice.model.domain.Room;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoomReservationDto {

    private UUID reservationId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

}
