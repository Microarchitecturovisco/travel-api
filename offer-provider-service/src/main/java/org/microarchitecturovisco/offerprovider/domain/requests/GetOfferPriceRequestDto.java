package org.microarchitecturovisco.offerprovider.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.CateringOptionDto;
import org.microarchitecturovisco.offerprovider.domain.dto.RoomsConfigurationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOfferPriceRequestDto {
    String dateFrom;
    String dateTo;

    Integer adults;
    Integer infants;
    Integer kids;
    Integer teens;

    private RoomsConfigurationDto roomConfiguration;
    private CateringOptionDto cateringOption;
    private List<TransportDto> departure;

}
