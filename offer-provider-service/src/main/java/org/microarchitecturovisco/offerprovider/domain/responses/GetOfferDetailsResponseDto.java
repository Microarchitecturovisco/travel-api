package org.microarchitecturovisco.offerprovider.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.CateringOptionDto;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.RoomResponseDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOfferDetailsResponseDto {
    private UUID idHotel;
    private String hotelName;
    private String description;
    private Float price;
    private LocationDto destination;
    private List<String> imageUrls;

    private List<RoomResponseDto> rooms;
    private Float pricePerAdult;

    private List<CateringOptionDto> cateringOptions;
}
