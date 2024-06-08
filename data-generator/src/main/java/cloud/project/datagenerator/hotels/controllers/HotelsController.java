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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.logging.Logger;

@RestController()
@RequiredArgsConstructor
public class HotelsController {

    private final HotelRepository hotelRepository;
    private final RoomReservationRepository roomReservationRepository;
    Logger logger = Logger.getLogger("DataGenerator | Hotels");

    @RabbitListener(queues = "#{handleCreateHotelReservationQueue.name}")
    public void consumeMessageCreateHotelReservation(String requestJson) {
        CreateHotelReservationRequest request = JsonReader.readCreateHotelReservationRequestCommand(requestJson);
        logger.info("Creating hotel reservations: " + request);

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
                roomReservation.setId(request.getReservationId());
                roomReservation.setDateFrom(request.getHotelTimeFrom());
                roomReservation.setDateTo(request.getHotelTimeTo());
                roomReservation.setRoom(room);
                roomReservation.setMainReservationId(request.getReservationId());

                room.getRoomReservations().add(roomReservation);
                roomReservationRepository.save(roomReservation);
            }
        }

        int countAfter = hotel.getRooms().stream()
                .mapToInt(room -> room.getRoomReservations().size()).sum();

        logger.info("consumeMessageCreateHotelReservation: " + countBefore + " --> " + countAfter);
    }

    @RabbitListener(queues = "#{handleDeleteHotelReservationQueue.name}")
    public void consumeMessageDeleteHotelReservation(String requestJson) {

        DeleteHotelReservationRequest request = JsonReader.readDeleteHotelReservationRequestCommand(requestJson);
        logger.info("Deleting hotel reservations: " + request);

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

        logger.info("consumeMessageDeleteHotelReservation: " + countBefore + " --> " + countAfter);
    }
}
