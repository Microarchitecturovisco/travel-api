package org.microarchitecturovisco.transport.model.cqrs.commands;

import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.time.LocalDateTime;

public class CreateTransportCommand {
    private String uuid;
    private LocalDateTime commandCreatedTime;

    private TransportDto transportDto;
}
