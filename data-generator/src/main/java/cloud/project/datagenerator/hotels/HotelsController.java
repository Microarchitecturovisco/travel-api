package cloud.project.datagenerator.hotels;

import cloud.project.datagenerator.websockets.DataGeneratorWebSocketHandler;
import cloud.project.datagenerator.websockets.HotelUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data-generator")
public class HotelsController {
    private final DataGeneratorWebSocketHandler dataGeneratorWebSocketHandler;

    public void updateHotelUpdatesOnFrontend(String updateType, String roomName, String hotelName, int capacityChange, float priceChange) {
        LocalDateTime currentDateAndTime = LocalDateTime.now().withSecond(0).withNano(0);

        HotelUpdate hotelUpdate = HotelUpdate.builder()
                .updateDateTime(currentDateAndTime)
                .updateType(updateType)
                .hotelName(hotelName)
                .roomName(roomName)
                .priceChange(priceChange)
                .capacityChange(capacityChange)
                .build();

        dataGeneratorWebSocketHandler.updateHotelList(hotelUpdate);
    }
}
