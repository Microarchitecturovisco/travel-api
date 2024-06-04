package cloud.project.datagenerator.hotels;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import cloud.project.datagenerator.rabbitmq.QueuesConfig;
import cloud.project.datagenerator.rabbitmq.json.JsonConverter;
import cloud.project.datagenerator.rabbitmq.requests.DataUpdateType;
import cloud.project.datagenerator.rabbitmq.requests.RoomUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HotelsDataGenerator {

    private final HotelRepository hotelRepository;
    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
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
    }

    private void updateRandomRoom() {
        Hotel randomHotel = getRandomHotel();
        if (randomHotel == null) return;

        Room randomRoom = randomHotel.getRooms().get(random.nextInt(randomHotel.getRooms().size()));
        int currentGuestCapacity = randomRoom.getGuestCapacity();
        int newGuestCapacity = random.nextInt(1, currentGuestCapacity + 10);

        float currentPricePerAdult = randomRoom.getPricePerAdult();
        float newPricePerAdult = random.nextFloat(currentPricePerAdult, currentPricePerAdult*10);

        randomRoom.setGuestCapacity(newGuestCapacity);
        randomRoom.setPricePerAdult(newPricePerAdult);

        updateHotelDataInHotelModules(DataUpdateType.UPDATE, randomRoom);
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
                .updateType(updateType)
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .name(room.getName())
                .guestCapacity(room.getGuestCapacity())
                .pricePerAdult(room.getPricePerAdult())
                .description(room.getDescription())
                .build();

        String roomUpdateRequestJson = JsonConverter.convert(roomUpdateRequest);

        System.out.println(updateType + " - Room: " + roomUpdateRequestJson);

        rabbitTemplate.convertAndSend(QueuesConfig.EXCHANGE_HOTEL_FANOUT_UPDATE_DATA, "", roomUpdateRequestJson);

        sendUpdateToFrontend(updateType, room);
    }

    private void sendUpdateToFrontend(DataUpdateType updateType, Room room){
        //TODO: here send a message to frontend via websocket
    }
}
