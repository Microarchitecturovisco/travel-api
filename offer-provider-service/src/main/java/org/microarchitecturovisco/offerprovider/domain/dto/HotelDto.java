package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
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
    private float pricePerAdult;
}
