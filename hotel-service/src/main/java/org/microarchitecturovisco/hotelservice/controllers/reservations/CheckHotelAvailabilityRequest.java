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
public class CheckHotelAvailabilityRequest {
    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private UUID hotelId;

    private List<UUID> roomReservationsIds;
}
