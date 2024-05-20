package org.microarchitecturovisco.hotelservice.model.events;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoomReservationDeletedEvent extends HotelEvent {
    private UUID idRoomReservation;
    private UUID idRoom;
}
