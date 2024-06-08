package cloud.project.datagenerator.rabbitmq.requests.hotels;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DeleteHotelReservationRequest implements Serializable {
    private UUID reservationId;
    private UUID hotelId;
    private List<UUID> roomIds;
}
