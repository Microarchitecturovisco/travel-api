package cloud.project.datagenerator.transports;

import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportCourse;
import cloud.project.datagenerator.transports.repositories.TransportCourseRepository;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransportsDataGenerator {
    enum DataUpdateType {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;
    private final TransportRepository transportRepository;
    private final TransportCourseRepository transportCourseRepository;

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void generateRandomTransportData() {
        System.out.println("Generating Transport Data...");

        int action = random.nextInt(3);

        switch (action) {
            case 0:
                createNewTransport();
                break;
            case 1:
                updateRandomTransport();
                break;
            case 2:
                deleteRandomTransport();
                break;
        }
    }

    private void createNewTransport() {
        TransportCourse randomTransportCourse = getRandomTransportCourse();
        if (randomTransportCourse == null) return;

        Transport newTransport = Transport.builder()
                .id(UUID.randomUUID())
                .course(randomTransportCourse)
                .departureDate(LocalDateTime.now().plusDays(1))
                .capacity(random.nextInt(1, 10))
                .pricePerAdult(random.nextFloat(200, 1000))
                .build();

        System.out.println("Creating new transport: " + newTransport);

        updateTransportDataInTransportModules(DataUpdateType.CREATE, newTransport);
    }


    private void updateRandomTransport() {
        Transport randomTransport = getRandomTransport();
        if (randomTransport == null) return;

        int currentGuestCapacity = randomTransport.getCapacity();
        int newCapacity = random.nextInt(0, (int) (currentGuestCapacity * random.nextDouble(0.1, 1.5)));

        System.out.println("Updating transport " + randomTransport.getId() +
                " - old capacity: " + currentGuestCapacity + " new capacity: " + newCapacity);

        randomTransport.setCapacity(newCapacity);

        updateTransportDataInTransportModules(DataUpdateType.UPDATE, randomTransport);

    }

    private void deleteRandomTransport() {
        Transport randomTransport = getRandomTransport();
        if (randomTransport == null) return;

        System.out.println("Deleting transport: " + randomTransport.getId());

        updateTransportDataInTransportModules(DataUpdateType.DELETE, randomTransport);
    }

    private TransportCourse getRandomTransportCourse() {
        List<TransportCourse> transportCourses = transportCourseRepository.findAll();

        if (transportCourses.isEmpty()) {
            System.out.println("No transport courses found.");
            return null;
        }

        return transportCourses.get(random.nextInt(transportCourses.size()));
    }

    private Transport getRandomTransport() {
        List<Transport> transports = transportRepository.findAll();

        if (transports.isEmpty()) {
            System.out.println("No transports found.");
            return null;
        }

        return transports.get(random.nextInt(transports.size()));
    }


    public void updateTransportDataInTransportModules(DataUpdateType updateType, Transport transport) {
        // todo
    }

}
