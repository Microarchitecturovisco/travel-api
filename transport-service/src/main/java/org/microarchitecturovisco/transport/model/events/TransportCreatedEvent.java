package org.microarchitecturovisco.transport.model.events;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transport_created_events")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class TransportCreatedEvent extends TransportEvent {

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private UUID idTransportCourse;
    private TransportType type;

    private UUID idDepartureLocation;
    private String departureLocationCountry;
    private String departureLocationRegion;

    private UUID idArrivalLocation;
    private String arrivalLocationCountry;
    private String arrivalLocationRegion;

    public TransportCreatedEvent(UUID idEvent, LocalDateTime eventTimeStamp, TransportDto dto) {
        this.setId(idEvent);
        this.setEventTimeStamp(eventTimeStamp);

        this.setIdTransport(dto.getIdTransport());
        this.setDepartureDate(dto.getDepartureDate());
        this.setCapacity(dto.getCapacity());
        this.setPricePerAdult(dto.getPricePerAdult());

        this.setIdTransportCourse(dto.getTransportCourse().getIdTransportCourse());
        this.setType(dto.getTransportCourse().getType());

        this.setIdDepartureLocation(dto.getTransportCourse().getDepartureFromLocation().getIdLocation());
        this.setDepartureLocationCountry(dto.getTransportCourse().getDepartureFromLocation().getCountry());
        this.setDepartureLocationRegion(dto.getTransportCourse().getDepartureFromLocation().getRegion());

        this.setIdArrivalLocation(dto.getTransportCourse().getArrivalAtLocation().getIdLocation());
        this.setArrivalLocationCountry(dto.getTransportCourse().getArrivalAtLocation().getCountry());
        this.setArrivalLocationRegion(dto.getTransportCourse().getArrivalAtLocation().getRegion());
    }
}
