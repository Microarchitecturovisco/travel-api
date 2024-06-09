package cloud.project.datagenerator.websockets.hotels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelUpdate implements Serializable {
    private String updateDateTime;
    private String updateType;
    private String hotelName;
    private String roomName;
    private float priceChange;
    private int capacityChange;
}
