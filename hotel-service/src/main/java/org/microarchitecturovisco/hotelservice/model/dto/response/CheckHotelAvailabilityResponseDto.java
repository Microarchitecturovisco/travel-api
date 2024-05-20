package org.microarchitecturovisco.hotelservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckHotelAvailabilityResponseDto implements Serializable {
    private boolean ifAvailable;
}
