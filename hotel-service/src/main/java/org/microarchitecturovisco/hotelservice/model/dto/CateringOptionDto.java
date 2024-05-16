package org.microarchitecturovisco.hotelservice.model.dto;


import lombok.*;
import org.microarchitecturovisco.hotelservice.model.domain.CateringType;
import lombok.Builder;
import lombok.Data;
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
