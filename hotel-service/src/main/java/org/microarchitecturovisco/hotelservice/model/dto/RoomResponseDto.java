package org.microarchitecturovisco.hotelservice.model.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoomResponseDto {
    @Id
    private UUID roomId;

    private String name;

    private int guestCapacity;

    @Lob
    private String description;
}
