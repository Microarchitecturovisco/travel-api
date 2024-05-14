package org.microarchitecturovisco.hotelservice.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class GetHotelsBySearchQueryResponseDto implements Serializable {
    private String uuid;
    private List<HotelDto> hotels;
    private List<Float> pricePerAdult;
}
