package org.microarchitecturovisco.reservationservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.reservationservice.domain.dto.HandleReservationRequestDto;
import org.microarchitecturovisco.reservationservice.domain.dto.HandleReservationResponseDto;


public class JsonConverter {
    public static String convertHandleReservationResponseDto (HandleReservationResponseDto dto){
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json;
        try {
            json = mapper.writeValueAsString(dto);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json failed to convert to JSON.");
        }
        return json;
    }

    public static  String convertHandleReservationRequestDto (HandleReservationRequestDto dto){
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json;
        try {
            json = mapper.writeValueAsString(dto);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json failed to convert to JSON.");
        }
        return json;
    }
}
