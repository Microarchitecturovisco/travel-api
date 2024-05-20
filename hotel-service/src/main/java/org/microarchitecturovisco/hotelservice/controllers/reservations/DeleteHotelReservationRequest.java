package org.microarchitecturovisco.hotelservice.controllers.reservations;

import lombok.*;

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
}
