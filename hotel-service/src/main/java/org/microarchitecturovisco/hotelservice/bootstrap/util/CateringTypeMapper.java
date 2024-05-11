package org.microarchitecturovisco.hotelservice.bootstrap.util;

import org.microarchitecturovisco.hotelservice.domain.CateringType;

public class CateringTypeMapper {
    public CateringType mapToCateringType(String foodOption) {
        if (foodOption.contains("All inclusive")) {
            return CateringType.ALL_INCLUSIVE;
        }

        return switch (foodOption) {
            case "Full board plus", "Half board plus" -> CateringType.THREE_COURSES;
            case "2 posiłki" -> CateringType.TWO_COURSES;
            case "Śniadania" -> CateringType.BREAKFAST;
            case "Bez wyżywienia" -> CateringType.NO_CATERING;
            default -> null;
        };
    }
}
