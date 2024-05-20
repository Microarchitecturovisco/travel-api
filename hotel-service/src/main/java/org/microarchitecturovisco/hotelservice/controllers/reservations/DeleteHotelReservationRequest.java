package org.microarchitecturovisco.hotelservice.controllers.reservations;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DeleteHotelReservationRequest {
    private UUID reservationId;
    private UUID hotelId;
    private List<UUID> roomIds;
}
