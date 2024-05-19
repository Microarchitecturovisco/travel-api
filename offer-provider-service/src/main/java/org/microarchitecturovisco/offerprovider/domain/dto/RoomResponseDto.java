package org.microarchitecturovisco.offerprovider.domain.dto;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponseDto {
    private UUID roomId;

    private String name;

    private int guestCapacity;

    private String description;
}
