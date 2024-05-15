package org.microarchitecturovisco.paymentservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentResponseDto;

public class JsonConverter {
    public static String convertHandlePaymentResponseDto (HandlePaymentResponseDto dto){
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

    public static String convertHandlePaymentRequestDto (HandlePaymentRequestDto dto){
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
