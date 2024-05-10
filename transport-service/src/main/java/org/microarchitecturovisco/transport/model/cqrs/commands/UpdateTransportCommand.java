package org.microarchitecturovisco.transport.model.cqrs.commands;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateTransportCommand {
    private String uuid;
    private LocalDateTime commandCreatedTime;

    private TransportDto transportDto;
}
