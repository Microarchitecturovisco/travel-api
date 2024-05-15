package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HotelDto implements Serializable {
    private UUID hotelId;

    private String name;

    private float rating;
    @Lob
    private String description;

    private LocationDto location;

    private List<CateringOptionDto> cateringOptions;

    private List<String> photos;
    
    private List<RoomDto> rooms;
}