package org.microarchitecturovisco.hotelservice.model.events;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
public abstract class HotelEvent {
    @Id
    private UUID id;

    private LocalDateTime eventTimeStamp;

    private UUID idHotel;
}
