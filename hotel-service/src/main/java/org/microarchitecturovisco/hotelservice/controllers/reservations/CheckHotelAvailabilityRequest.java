package org.microarchitecturovisco.hotelservice.controllers.reservations;

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
public class CheckHotelAvailabilityRequest implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private UUID hotelId;
    private List<UUID> roomReservationsIds;
}
