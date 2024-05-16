package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class HotelDto implements Serializable {
    private UUID hotelId;
    private String name;
    private float rating;
    private String description;
    private LocationDto location;
    private List<String> photos;
    private float pricePerAdult;
}
