package cloud.project.datagenerator.hotels.controllers;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.domain.RoomReservation;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import cloud.project.datagenerator.rabbitmq.json.JsonReader;
import cloud.project.datagenerator.rabbitmq.requests.hotels.CreateHotelReservationRequest;
import cloud.project.datagenerator.rabbitmq.requests.hotels.DeleteHotelReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class HotelsController {

    private final HotelRepository hotelRepository;

    @RabbitListener(queues = "#{handleCreateHotelReservationQueue.name}")
    public void consumeMessageCreateHotelReservation(String requestJson) {
        CreateHotelReservationRequest request = JsonReader.readCreateHotelReservationRequestCommand(requestJson);
        System.out.println("Creating hotel reservations: " + request);

        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setId(request.getReservationId());
        roomReservation.setDateFrom(request.getHotelTimeFrom());
        roomReservation.setDateTo(request.getHotelTimeTo());

        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElse(null);
        assert hotel != null;

        List<Room> roomsInHotel = hotel.getRooms();

        for (UUID roomId : request.getRoomIds()) {
            for (Room room : roomsInHotel) {
                if(room.getId().equals(roomId)) {
                    room.getRoomReservations().add(roomReservation);
                }
            }
        }
    }

    @RabbitListener(queues = "#{handleDeleteHotelReservationQueue.name}")
    public void consumeMessageDeleteHotelReservation(String requestJson) {

        DeleteHotelReservationRequest request = JsonReader.readDeleteHotelReservationRequestCommand(requestJson);
        System.out.println("Deleting hotel reservations: " + request);

        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElse(null);
        assert hotel != null;

        List<Room> roomsInHotel = hotel.getRooms();
        for (UUID roomId : request.getRoomIds()) {
            for (Room room : roomsInHotel) {
                if(room.getId().equals(roomId)) {
                    List<RoomReservation> roomReservations = room.getRoomReservations();
                    roomReservations.removeIf(roomReservation -> roomReservation.getId().equals(request.getReservationId()));
                }
            }
        }
    }
}
