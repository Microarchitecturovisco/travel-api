package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferDto {
    private Integer idHotel;
    private String hotelName;
    private Float price;
    private String destination;
    private Float rating;
    private String imageUrl;
}
