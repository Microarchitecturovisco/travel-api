package org.microarchitecturovisco.hotelservice.model.events;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.microarchitecturovisco.hotelservice.model.domain.Room;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_reservation_created_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoomReservationAddEvent extends RoomEvent {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Room room;
}
