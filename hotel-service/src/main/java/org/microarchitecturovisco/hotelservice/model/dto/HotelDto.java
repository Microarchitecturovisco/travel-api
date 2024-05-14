package org.microarchitecturovisco.hotelservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.List;

@Data
@Builder
public class HotelDto implements Serializable {
    private int hotelId;

    private String name;

    private float rating;

    private String description;

    private LocationDto location;

    private List<String> photos;
}
