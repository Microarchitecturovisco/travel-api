package cloud.project.datagenerator.websockets.transports;

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
public class TransportUpdate implements Serializable {
    private LocalDateTime updateDateTime;
    private String updateType;
    private String departureRegionAndCountry;
    private String arrivalRegionAndCountry;
    private String transportTypeName;
    private float priceChange;
    private int capacityChange;
}
