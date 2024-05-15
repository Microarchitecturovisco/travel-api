package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

import java.util.List;

@Data
@Builder
public class HotelDto implements Serializable {
    private UUID hotelId;

    private String name;

    private float rating;

    private String description;

    private LocationDto location;

    private List<CateringOptionDto> cateringOptions;

    private List<String> photos;
    
    private List<RoomDto> rooms;

    public HotelDto(UUID hotelId,
                    String name,
                    String description,
                    float rating,
                    LocationDto location,
                    List<String> photos){
        this.hotelId = hotelId;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.location = location;
        this.photos = photos;
    }

}