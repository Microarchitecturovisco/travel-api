package cloud.project.datagenerator.rabbitmq.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportUpdateRequest {

    private String updateType;

    private UUID id;

    private UUID courseId;

    private LocalDateTime departureDate;

    private int capacity;

    private float pricePerAdult;
}
