package org.microarchitecturovisco.transport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class GetTransportsBySearchQueryResponseDto implements Serializable {
    private String uuid;
    private List<TransportDto> transportDtoList;
}
