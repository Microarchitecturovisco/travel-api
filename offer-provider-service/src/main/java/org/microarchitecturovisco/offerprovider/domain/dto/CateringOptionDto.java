package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateringOptionDto {
    private UUID cateringId;

    private CateringType type;

    private float rating;

    private float price;

    private UUID hotelId;
}
