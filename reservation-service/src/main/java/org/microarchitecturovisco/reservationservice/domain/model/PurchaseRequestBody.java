package org.microarchitecturovisco.reservationservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestBody {
    private String reservationId;
    private String cardNumber;
}
