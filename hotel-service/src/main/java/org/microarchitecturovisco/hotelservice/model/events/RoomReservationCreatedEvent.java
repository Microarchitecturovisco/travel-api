package org.microarchitecturovisco.hotelservice.model.events;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoomReservationCreatedEvent extends HotelEvent {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private UUID idRoomReservation;
    private UUID idRoom;
}
