package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckHotelAvailabilityRequest implements Serializable {
    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int amountOfQuests;

    private UUID hotelId;
}
