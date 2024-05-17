package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto implements Serializable {
    private UUID hotelId;
    private String name;
    private float rating;
    private String description;
    private LocationDto location;
    private List<String> photos;
    private float pricePerAdult;
}
