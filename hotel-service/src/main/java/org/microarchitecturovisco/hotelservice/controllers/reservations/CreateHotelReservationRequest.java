package org.microarchitecturovisco.hotelservice.controllers.reservations;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CreateHotelReservationRequest {
    private UUID reservationId;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private List<UUID> roomReservationsIds;

    private UUID hotelId;
}
