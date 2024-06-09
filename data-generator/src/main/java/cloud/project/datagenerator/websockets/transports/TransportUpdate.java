package cloud.project.datagenerator.websockets.transports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportUpdate implements Serializable {
    private String updateDateTime;
    private String updateType;
    private String departureRegionAndCountry;
    private String arrivalRegionAndCountry;
    private String transportTypeName;
    private float priceChange;
    private int capacityChange;
}
