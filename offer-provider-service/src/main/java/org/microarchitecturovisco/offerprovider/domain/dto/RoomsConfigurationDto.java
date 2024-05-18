package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class RoomsConfigurationDto {
    private List<RoomResponseDto> rooms;
    private Float pricePerAdult;
}
