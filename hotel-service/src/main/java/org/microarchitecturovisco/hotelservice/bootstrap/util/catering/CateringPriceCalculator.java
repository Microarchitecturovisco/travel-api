package org.microarchitecturovisco.hotelservice.bootstrap.util.catering;

import org.microarchitecturovisco.hotelservice.model.domain.CateringType;

public class CateringPriceCalculator {

    public float calculateCateringPrice(CateringType cateringType) {
        return switch (cateringType) {
            case ALL_INCLUSIVE -> 100.0f;
            case THREE_COURSES -> 80.0f;
            case TWO_COURSES -> 60.0f;
            case BREAKFAST -> 30.0f;
            case NO_CATERING -> 0.0f;
            default -> 0.0f;
        };
    }
}