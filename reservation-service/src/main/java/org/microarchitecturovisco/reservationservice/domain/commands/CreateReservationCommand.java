package org.microarchitecturovisco.reservationservice.domain.commands;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class CreateReservationCommand {
    @Id
    private UUID id;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;
    private float price;
    private boolean paid;
    private UUID hotelId;
    @ElementCollection
    private List<UUID> roomReservationsIds;
    @ElementCollection
    private List<UUID> transportReservationsIds;
    private UUID userId;
}
