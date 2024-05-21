package org.microarchitecturovisco.reservationservice.domain.dto.responses;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckHotelAvailabilityResponseDto implements Serializable {
    private boolean ifAvailable;
}
