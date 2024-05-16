package org.microarchitecturovisco.hotelservice.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.dto.HotelResponseDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class GetHotelsBySearchQueryResponseDto implements Serializable {
    private List<HotelResponseDto> hotels;
}
