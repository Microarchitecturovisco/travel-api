package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHotelReservationRequest implements Serializable {
    private UUID reservationId;
    private UUID hotelId;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private List<UUID> roomIds;
}
