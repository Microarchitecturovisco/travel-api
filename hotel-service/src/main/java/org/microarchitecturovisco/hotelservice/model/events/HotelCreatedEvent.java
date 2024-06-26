package org.microarchitecturovisco.hotelservice.model.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Lob
    private String description;
    @ElementCollection
    private List<String> photos = new ArrayList<>();

    private UUID idLocation;
    private String country;
    private String region;


    public HotelCreatedEvent(LocalDateTime eventTimeStamp, HotelDto hotelDto) {
        this.setEventTimeStamp(eventTimeStamp);

        this.setIdHotel(hotelDto.getHotelId());
        this.setName(hotelDto.getName());
        this.setRating(hotelDto.getRating());
        this.setDescription(hotelDto.getDescription());

        this.setIdLocation(hotelDto.getLocation().getIdLocation());
        this.setCountry(hotelDto.getLocation().getCountry());
        this.setRegion(hotelDto.getLocation().getRegion());
        this.setPhotos(hotelDto.getPhotos());

    }
}
