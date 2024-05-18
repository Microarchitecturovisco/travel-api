package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.CateringOptionDto;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.RoomsConfigurationDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelDetailsResponseDto implements Serializable {
    private UUID hotelId;
    private String hotelName;

    private float rating;

    private String description;

    private LocationDto location;

    private List<CateringOptionDto> cateringOptions;

    private List<String> photos;

    private List<RoomsConfigurationDto> roomsConfigurations;
}
