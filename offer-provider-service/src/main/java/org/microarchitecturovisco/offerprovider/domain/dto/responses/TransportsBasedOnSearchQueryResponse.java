package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportsBasedOnSearchQueryResponse {
    private UUID uuid;
    private List<List<TransportDto>> transportPairs;
}
