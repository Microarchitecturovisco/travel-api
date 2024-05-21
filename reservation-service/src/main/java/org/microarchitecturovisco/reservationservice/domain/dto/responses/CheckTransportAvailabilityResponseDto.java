package org.microarchitecturovisco.reservationservice.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransportAvailabilityResponseDto implements Serializable {
    private boolean ifAvailable;
}
