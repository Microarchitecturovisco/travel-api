package org.microarchitecturovisco.hotelservice.model.events;

import org.microarchitecturovisco.hotelservice.model.domain.CateringType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CateringOptionCreatedEvent extends HotelEvent {
    private UUID idCatering;
    private CateringType type;
    private float rating;
    private float price;
}
