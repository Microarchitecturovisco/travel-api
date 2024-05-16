package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class RoomsConfigurationDto {
    private List<RoomDto> rooms;
}
