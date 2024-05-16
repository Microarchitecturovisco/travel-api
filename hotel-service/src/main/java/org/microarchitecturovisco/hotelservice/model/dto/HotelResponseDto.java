package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class HotelResponseDto implements Serializable {
    private UUID hotelId;

    private String name;

    private float rating;
    @Lob
    private String description;

    private LocationDto location;

    private List<String> photos;

    private float pricePerAdult;
}
