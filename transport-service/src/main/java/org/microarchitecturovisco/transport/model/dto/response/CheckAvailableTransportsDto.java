package org.microarchitecturovisco.transport.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.util.List;

@Data
@Builder
public class CheckAvailableTransportsDto {
    private List<TransportDto> transports;
}
