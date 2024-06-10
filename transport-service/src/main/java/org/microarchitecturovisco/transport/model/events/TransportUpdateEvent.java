package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class TransportUpdateEvent extends TransportEvent {
    private int capacity;
    private float pricePerAdult;

    public TransportUpdateEvent(UUID transportId, int capacity, float pricePerAdult){
        this.setEventTimeStamp(LocalDateTime.now());
        this.setIdTransport(transportId);
        this.capacity = capacity;
        this.pricePerAdult = pricePerAdult;
    }
}
