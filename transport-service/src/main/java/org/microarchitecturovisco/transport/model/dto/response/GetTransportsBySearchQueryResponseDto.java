package org.microarchitecturovisco.transport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetTransportsBySearchQueryResponseDto implements Serializable {
    private UUID uuid;
    private List<TransportDto> transportDtoList;
}
