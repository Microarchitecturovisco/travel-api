package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.domain.CateringOption;
import org.microarchitecturovisco.hotelservice.model.domain.Location;
import org.microarchitecturovisco.hotelservice.model.domain.Room;

import java.io.Serializable;
import java.time.LocalDateTime;
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


}