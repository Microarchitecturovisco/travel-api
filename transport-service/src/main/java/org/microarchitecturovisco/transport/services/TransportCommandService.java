package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportCommandService {

    private final TransportEventSourcingHandler transportEventSourcingHandler;

    public void save(UUID idTransport) {
        transportEventSourcingHandler.project(idTransport);
    }

}
