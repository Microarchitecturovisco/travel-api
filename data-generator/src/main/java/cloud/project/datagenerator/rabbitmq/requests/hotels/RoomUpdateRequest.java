package cloud.project.datagenerator.rabbitmq.requests.hotels;

import cloud.project.datagenerator.rabbitmq.requests.DataUpdateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomUpdateRequest {

    private DataUpdateType updateType;

    private UUID id;

    private UUID hotelId;

    private String name;

    private int guestCapacity;

    private float pricePerAdult;

    private String description;

    // List of room reservations for each room is empty
}
