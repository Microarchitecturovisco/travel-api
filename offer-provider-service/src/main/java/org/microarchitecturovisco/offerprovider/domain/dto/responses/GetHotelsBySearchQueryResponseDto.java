package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.HotelDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelsBySearchQueryResponseDto implements Serializable {
    private List<HotelDto> hotels;
}
