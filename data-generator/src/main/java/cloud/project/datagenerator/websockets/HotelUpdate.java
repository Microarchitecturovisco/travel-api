package cloud.project.datagenerator.websockets;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class HotelUpdate {
    private LocalDateTime updateDateTime;
    private String updateType;
    private String hotelName;
    private String roomName;
    private float priceChange;
    private int capacityChange;
}
