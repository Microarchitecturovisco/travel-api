package org.microarchitecturovisco.hotelservice.model.dto;

import org.microarchitecturovisco.hotelservice.model.domain.CateringType;

import java.util.UUID;

public class CateringOptionDto {
    private UUID catteringId;

    private CateringType type;

    private float rating;

    private float price;
}
