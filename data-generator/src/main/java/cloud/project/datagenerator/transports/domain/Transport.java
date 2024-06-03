package cloud.project.datagenerator.transports.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transport {
    @Id
    private UUID id;

    @ManyToOne
    private TransportCourse course;

    // the day the transport takes place
    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private int capacity;

    @NotNull
    private float pricePerAdult;
}
