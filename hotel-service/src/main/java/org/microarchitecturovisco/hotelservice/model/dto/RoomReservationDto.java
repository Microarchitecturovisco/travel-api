package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.domain.Room;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RoomReservationDto {

    private UUID reservationId;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

}
