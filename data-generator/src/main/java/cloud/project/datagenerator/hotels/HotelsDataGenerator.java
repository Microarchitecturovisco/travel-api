package cloud.project.datagenerator.hotels;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import cloud.project.datagenerator.rabbitmq.QueuesConfigHotels;
import cloud.project.datagenerator.rabbitmq.json.JsonConverter;
import cloud.project.datagenerator.rabbitmq.requests.hotels.RoomUpdateRequest;
import cloud.project.datagenerator.websockets.hotels.DataGeneratorHotelsWebSocketHandler;
import cloud.project.datagenerator.websockets.hotels.HotelUpdate;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HotelsDataGenerator {
    enum DataUpdateType {
        CREATE,
        UPDATE
    }

    private final HotelRepository hotelRepository;
    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;
    private final DataGeneratorHotelsWebSocketHandler dataGeneratorHotelsWebSocketHandler;

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void updateRandomHotelData() {
        int action = random.nextInt(2);

        switch (action) {
            case 0:
                createNewRoom();
                break;
            case 1:
                updateRandomRoom();
                break;
        }
    }

    private void createNewRoom() {
        Hotel randomHotel = getRandomHotel();
        if (randomHotel == null) return;

        Room newRoom = Room.builder()
                .id(UUID.randomUUID())
                .hotel(randomHotel)
                .name("NewRoom-" + random.nextInt(100))
                .guestCapacity(random.nextInt(3, 10))
                .pricePerAdult(random.nextFloat() * 100)
                .description("This is an awesome new room generated by Data Generator module")
                .build();

        updateHotelDataInHotelModules(DataUpdateType.CREATE, newRoom);

        updateHotelUpdatesOnFrontend(DataUpdateType.CREATE, newRoom.getName(), randomHotel.getName(), 0, 0);
    }

    private void updateRandomRoom() {
        Pair<Hotel, Room> randomRoomPair = getRandomRoomWithoutReservations();
        Hotel randomHotel = randomRoomPair.getKey();
        Room randomRoom = randomRoomPair.getValue();

        int currentGuestCapacity = randomRoom.getGuestCapacity();
        int newGuestCapacity = random.nextInt(currentGuestCapacity, currentGuestCapacity + 10);

        float currentPricePerAdult = randomRoom.getPricePerAdult();
        float newPricePerAdult = random.nextFloat(currentPricePerAdult, currentPricePerAdult * 10);

        randomRoom.setGuestCapacity(newGuestCapacity);
        randomRoom.setPricePerAdult(newPricePerAdult);

        updateHotelDataInHotelModules(DataUpdateType.UPDATE, randomRoom);

        int capacityChange = newGuestCapacity - currentGuestCapacity;
        float priceChange = newPricePerAdult - currentPricePerAdult;

        updateHotelUpdatesOnFrontend(DataUpdateType.UPDATE, randomRoom.getName(), randomHotel.getName(), capacityChange, priceChange);
    }


    private Pair<Hotel, Room> getRandomRoomWithoutReservations() {
        List<Hotel> hotels = hotelRepository.findAll();

        if (hotels.isEmpty()) {
            System.out.println("No hotels found.");
            return null;
        }

        List<Pair<Hotel, Room>> roomsWithoutReservations = new ArrayList<>();
        for (Hotel hotel : hotels) {
            for (Room room : hotel.getRooms()) {
                if (room.getRoomReservations().isEmpty()) {
                    roomsWithoutReservations.add(new Pair<>(hotel, room));
                }
            }
        }

        if (roomsWithoutReservations.isEmpty()) {
            System.out.println("No rooms found without reservations.");
            return null;
        }

        return roomsWithoutReservations.get(random.nextInt(roomsWithoutReservations.size()));
    }

    private Hotel getRandomHotel() {
        List<Hotel> hotels = hotelRepository.findAll();

        if (hotels.isEmpty()) {
            System.out.println("No hotels found.");
            return null;
        }

        Hotel randomHotel;
        int attempts = 0;
        do {
            randomHotel = hotels.get(random.nextInt(hotels.size()));
            attempts++;
        } while (randomHotel.getRooms().isEmpty() && attempts < hotels.size());

        if (randomHotel.getRooms().isEmpty()) {
            System.out.println("No rooms found in the selected hotel.");
            return null;
        }

        return randomHotel;
    }

    public void updateHotelDataInHotelModules(DataUpdateType updateType, Room room) {
        RoomUpdateRequest roomUpdateRequest = RoomUpdateRequest.builder()
                .updateType(String.valueOf(updateType))
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .name(room.getName())
                .guestCapacity(room.getGuestCapacity())
                .pricePerAdult(room.getPricePerAdult())
                .description(room.getDescription())
                .build();

        String roomUpdateRequestJson = JsonConverter.convert(roomUpdateRequest);

//        System.out.println(updateType + " - Room: " + roomUpdateRequestJson);

        rabbitTemplate.convertAndSend(QueuesConfigHotels.EXCHANGE_HOTEL_FANOUT_UPDATE_DATA, "", roomUpdateRequestJson);
    }

    private void updateHotelUpdatesOnFrontend(DataUpdateType updateType, String roomName, String hotelName, int capacityChange, float priceChange) {
        LocalDateTime currentDateAndTime = LocalDateTime.now().withSecond(0).withNano(0);

        HotelUpdate hotelUpdate = HotelUpdate.builder()
                .updateDateTime(currentDateAndTime)
                .updateType(String.valueOf(updateType))
                .hotelName(hotelName)
                .roomName(roomName)
                .priceChange(priceChange)
                .capacityChange(capacityChange)
                .build();

        dataGeneratorHotelsWebSocketHandler.updateHotelList(hotelUpdate);
    }
}
