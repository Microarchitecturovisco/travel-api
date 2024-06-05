package cloud.project.datagenerator.rabbitmq.json;


import cloud.project.datagenerator.rabbitmq.requests.hotels.CreateHotelReservationRequest;
import cloud.project.datagenerator.rabbitmq.requests.hotels.DeleteHotelReservationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonReader {
    public static CreateHotelReservationRequest readCreateHotelReservationRequestCommand(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, CreateHotelReservationRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }

    public static DeleteHotelReservationRequest readDeleteHotelReservationRequestCommand(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, DeleteHotelReservationRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
}
