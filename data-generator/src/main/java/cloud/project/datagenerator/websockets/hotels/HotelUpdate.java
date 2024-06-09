package cloud.project.datagenerator.websockets.hotels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelUpdate implements Serializable {
    private LocalDateTime updateDateTime;
    private String updateType;
    private String hotelName;
    private String roomName;
    private float priceChange;
    private int capacityChange;
}
