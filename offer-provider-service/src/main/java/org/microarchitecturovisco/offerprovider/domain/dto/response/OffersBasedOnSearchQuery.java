package org.microarchitecturovisco.offerprovider.domain.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;

import java.util.List;

@Data
@Builder
public class OffersBasedOnSearchQuery {
    private List<OfferDto> offers;
}
