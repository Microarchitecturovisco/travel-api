package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "transport_created_events")
@RequiredArgsConstructor
@Builder
public class TransportCreatedEvent extends TransportEvent {

    // TODO
}
