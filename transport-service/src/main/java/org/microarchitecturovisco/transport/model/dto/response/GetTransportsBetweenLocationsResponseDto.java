package org.microarchitecturovisco.transport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetTransportsBetweenLocationsResponseDto {
    private UUID uuid;
    private List<Pair<TransportDto, TransportDto>> transportPairs;
}
