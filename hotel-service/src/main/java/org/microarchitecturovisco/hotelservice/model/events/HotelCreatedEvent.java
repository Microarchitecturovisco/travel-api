package org.microarchitecturovisco.hotelservice.model.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.microarchitecturovisco.hotelservice.model.domain.CateringOption;
import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.domain.Location;
import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class HotelCreatedEvent extends HotelEvent {
    private UUID idHotel;
    private String name;
    private float rating;
    private String description;

    private UUID idLocation;
    private String country;
    private String region;

    public HotelCreatedEvent(UUID idEvent, LocalDateTime eventTimeStamp, HotelDto hotelDto) {
        this.setIdHotel(idEvent);
        this.setEventTimeStamp(eventTimeStamp);

        this.setIdHotel(hotelDto.getHotelId());
        this.setName(hotelDto.getName());
        this.setRating(hotelDto.getRating());
        this.setDescription(hotelDto.getDescription());

        this.setIdLocation(hotelDto.getLocation().getIdLocation());
        this.setCountry(hotelDto.getLocation().getCountry());
        this.setRegion(hotelDto.getLocation().getRegion());

    }
}
