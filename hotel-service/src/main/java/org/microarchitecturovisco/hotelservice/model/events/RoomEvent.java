package org.microarchitecturovisco.hotelservice.model.events;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RoomEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public final LocalDateTime created = LocalDateTime.now();

    private int idRoom;
}
