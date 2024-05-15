package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoomDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID roomId;

    private UUID hotelId;

    private String name;

    private int guestCapacity;

    private float pricePerAdult;

    private String description;

    private List<RoomReservationDto> roomReservations;

    public UUID getHotelId() {
        return this.roomId;
    }
}
