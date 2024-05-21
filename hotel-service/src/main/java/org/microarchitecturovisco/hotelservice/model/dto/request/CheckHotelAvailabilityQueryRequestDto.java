package org.microarchitecturovisco.hotelservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckHotelAvailabilityQueryRequestDto implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private UUID hotelId;
    private List<UUID> roomReservationsIds;
}
