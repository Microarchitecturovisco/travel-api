package org.microarchitecturovisco.hotelservice.model.dto.request;

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
public class CheckHotelAvailabilityRequestDto implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private List<UUID> roomIds;
    private UUID hotelId;
}
