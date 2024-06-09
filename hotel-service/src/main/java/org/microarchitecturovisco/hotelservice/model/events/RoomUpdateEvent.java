package org.microarchitecturovisco.hotelservice.model.events;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class RoomUpdateEvent extends HotelEvent{
    private UUID roomId;
    private String name;
    private int guestCapacity;
    private float pricePerAdult;
    @Lob
    private String description;

    public RoomUpdateEvent(UUID idHotel, UUID roomId, String name, int guestCapacity, float pricePerAdult,
                           String description) {
        this.setEventTimeStamp(LocalDateTime.now());
        this.setIdHotel(idHotel);

        this.roomId = roomId;
        this.name = name;
        this.guestCapacity = guestCapacity;
        this.pricePerAdult = pricePerAdult;
        this.description = description;
    }
}
