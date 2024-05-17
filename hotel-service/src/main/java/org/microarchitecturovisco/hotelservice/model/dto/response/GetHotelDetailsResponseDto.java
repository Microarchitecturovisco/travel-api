package org.microarchitecturovisco.hotelservice.model.dto.response;

import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.dto.CateringOptionDto;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomsConfigurationDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
@Data
@Builder
public class GetHotelDetailsResponseDto implements Serializable {
    private UUID hotelId;
    private String hotelName;

    private float rating;
    @Lob
    private String description;

    private LocationDto location;

    private List<CateringOptionDto> cateringOptions;

    private List<String> photos;

    private List<RoomsConfigurationDto> roomsConfigurations;
}
