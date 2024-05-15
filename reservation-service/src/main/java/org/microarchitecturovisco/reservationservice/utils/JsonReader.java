package org.microarchitecturovisco.reservationservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.reservationservice.domain.dto.HandleReservationRequestDto;

public class JsonReader {

    public static HandleReservationRequestDto handlePaymentRequestDtoFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, HandleReservationRequestDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
}
