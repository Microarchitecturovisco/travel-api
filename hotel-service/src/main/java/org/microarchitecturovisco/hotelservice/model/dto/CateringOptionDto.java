package org.microarchitecturovisco.hotelservice.model.dto;

import org.microarchitecturovisco.hotelservice.model.domain.CateringType;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CateringOptionDto {
    private UUID catteringId;

    private CateringType type;

    private float rating;

    private float price;
}
