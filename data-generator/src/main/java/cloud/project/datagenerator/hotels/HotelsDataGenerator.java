package cloud.project.datagenerator.hotels;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.domain.Room;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
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

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void generateRandomHotelData() {
        System.out.println("Generating Hotel Data...");

        int action = random.nextInt(3);

        switch (action) {
            case 0:
                createNewRoom();
                break;
            case 1:
                updateRandomRoom();
                break;
            case 2:
                deleteRandomRoom();
                break;
        }
    }

    private void createNewRoom() {
        Hotel randomHotel = getRandomHotel();
        if (randomHotel == null) return;

        Room newRoom = Room.builder()
                .id(UUID.randomUUID())
                .hotel(randomHotel)
                .name("NewRoom " + random.nextInt(100))
                .guestCapacity(random.nextInt(3, 10))
                .pricePerAdult(random.nextFloat() * 100)
                .description("This is a new room generated by Data Generator module")
                .build();

        System.out.println("Creating new room: " + newRoom);

        // Todo: send the room using rabbitmq

    }

    private void updateRandomRoom() {
        Hotel randomHotel = getRandomHotel();
        if (randomHotel == null) return;

        Room randomRoom = randomHotel.getRooms().get(random.nextInt(randomHotel.getRooms().size()));
        int currentGuestCapacity = randomRoom.getGuestCapacity();
        int newGuestCapacity = random.nextInt(1, currentGuestCapacity + 10);

        System.out.println("Updating room " + randomRoom.getName() + " in hotel " + randomHotel.getName() +
                " - old guest capacity: " + currentGuestCapacity + " new guest capacity: " + newGuestCapacity);

        // Todo: send the room using rabbitmq
        randomRoom.setGuestCapacity(newGuestCapacity);

    }

    private void deleteRandomRoom() {
        Hotel randomHotel = getRandomHotel();
        if (randomHotel == null) return;

        Room randomRoom = randomHotel.getRooms().get(random.nextInt(randomHotel.getRooms().size()));

        System.out.println("Deleting room: " + randomRoom);
        // Todo: send the room using rabbitmq

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

}
