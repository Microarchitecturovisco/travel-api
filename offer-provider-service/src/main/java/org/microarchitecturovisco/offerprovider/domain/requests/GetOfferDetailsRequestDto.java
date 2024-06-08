package org.microarchitecturovisco.offerprovider.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOfferDetailsRequestDto {
    UUID idHotel;
    String dateFrom;
    String dateTo;
    List<UUID> departureBuses;
    List<UUID> departurePlanes;
    Integer adults;
    Integer infants;
    Integer kids;
    Integer teens;
}
