package org.microarchitecturovisco.reservationservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HotelInfo {
    private String name;
    private Float hotelPrice;
    private Map<String, Integer> roomTypes;
}
