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
public class TransportOnlyCreatedEvent extends TransportEvent {
    private UUID courseId;
    private LocalDateTime departureDate;
    private int capacity;
    private float pricePerAdult;

    public TransportOnlyCreatedEvent(UUID transportId, UUID courseId, LocalDateTime departureDate, int capacity,
                                     float pricePerAdult) {
        this.setIdTransport(transportId);
        this.setEventTimeStamp(LocalDateTime.now());
        this.courseId = courseId;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.pricePerAdult = pricePerAdult;
    }
}
