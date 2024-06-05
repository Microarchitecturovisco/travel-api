package cloud.project.datagenerator.hotels.controllers;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.domain.RoomReservation;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import cloud.project.datagenerator.hotels.repositories.RoomReservationRepository;
import cloud.project.datagenerator.rabbitmq.json.JsonReader;
import cloud.project.datagenerator.rabbitmq.requests.hotels.CreateHotelReservationRequest;
import cloud.project.datagenerator.rabbitmq.requests.hotels.DeleteHotelReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping("")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelRepository hotelRepository;
    private final RoomReservationRepository roomReservationRepository;

    @RabbitListener(queues = "#{handleCreateHotelReservationQueue.name}")
    public void consumeMessageCreateHotelReservation(String requestJson) {
        CreateHotelReservationRequest request = JsonReader.readCreateHotelReservationRequestCommand(requestJson);
        System.out.println("Creating hotel reservations: " + request);

        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElse(null);
        assert hotel != null;

        int countBefore = hotel.getRooms().stream()
                .mapToInt(room -> room.getRoomReservations().size()).sum();

        for (UUID roomId : request.getRoomIds()) {
            Room room = hotel.getRooms().stream()
                    .filter(r -> r.getId().equals(roomId))
                    .findFirst()
                    .orElse(null);
            if (room != null) {
                RoomReservation roomReservation = new RoomReservation();
                roomReservation.setId(request.getReservationId()); // Generate new ID for each reservation
                roomReservation.setDateFrom(request.getHotelTimeFrom());
                roomReservation.setDateTo(request.getHotelTimeTo());
                roomReservation.setRoom(room);  // Set the room for the reservation
                roomReservation.setMainReservationId(request.getReservationId());

                room.getRoomReservations().add(roomReservation);
                roomReservationRepository.save(roomReservation);  // Save the reservation
            }
        }

        int countAfter = hotel.getRooms().stream()
                .mapToInt(room -> room.getRoomReservations().size()).sum();

        System.out.println("consumeMessageCreateHotelReservation: " + countBefore + " --> " + countAfter);
    }

    @RabbitListener(queues = "#{handleDeleteHotelReservationQueue.name}")
    public void consumeMessageDeleteHotelReservation(String requestJson) {

        DeleteHotelReservationRequest request = JsonReader.readDeleteHotelReservationRequestCommand(requestJson);
        System.out.println("Deleting hotel reservations: " + request);

        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElse(null);
        assert hotel != null;

        int countBefore = hotel.getRooms().stream()
                .mapToInt(room -> room.getRoomReservations().size()).sum();

        for (UUID roomId : request.getRoomIds()) {
            Room room = hotel.getRooms().stream()
                    .filter(r -> r.getId().equals(roomId))
                    .findFirst()
                    .orElse(null);
            if (room != null) {
                room.getRoomReservations().removeIf(roomReservation -> roomReservation.getId().equals(request.getReservationId()));
            }
        }

        int countAfter = hotel.getRooms().stream()
                .mapToInt(room -> room.getRoomReservations().size()).sum();

        System.out.println("consumeMessageDeleteHotelReservation: " + countBefore + " --> " + countAfter);
    }
}
