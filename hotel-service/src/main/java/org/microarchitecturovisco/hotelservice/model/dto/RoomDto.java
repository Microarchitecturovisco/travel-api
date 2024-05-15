package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.domain.RoomReservation;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoomDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID roomId;

    private String name;

    private int guestCapacity;

    private float pricePerAdult;

    private String description;

    private List<RoomReservationDto> roomReservations;
}