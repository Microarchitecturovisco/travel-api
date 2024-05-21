package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.*;

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
    private List<UUID> roomReservationsIds;
    private UUID hotelId;
}
