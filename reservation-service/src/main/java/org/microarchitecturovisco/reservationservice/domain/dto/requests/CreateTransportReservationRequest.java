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
public class CreateTransportReservationRequest implements Serializable {
    private UUID reservationId;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int amountOfQuests;
    private List<UUID> transportIds;
}
