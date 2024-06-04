package cloud.project.datagenerator.transports;

import cloud.project.datagenerator.rabbitmq.QueuesConfig;
import cloud.project.datagenerator.rabbitmq.json.JsonConverter;
import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportCourse;
import cloud.project.datagenerator.rabbitmq.requests.TransportUpdateRequest;
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
        UPDATE
    }

    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;
    private final TransportRepository transportRepository;
    private final TransportCourseRepository transportCourseRepository;

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void updateRandomTransportData() {
        int action = random.nextInt(2);

        switch (action) {
            case 0:
                createNewTransport();
                break;
            case 1:
                updateRandomTransport();
                break;
        }
    }

    private void createNewTransport() {
        TransportCourse randomTransportCourse = getRandomTransportCourse();
        if (randomTransportCourse == null) return;

        LocalDateTime departureDate = LocalDateTime.now().plusDays(1)
                .withSecond(0)
                .withNano(0);

        Transport newTransport = Transport.builder()
                .id(UUID.randomUUID())
                .course(randomTransportCourse)
                .departureDate(departureDate)
                .capacity(random.nextInt(1, 10))
                .pricePerAdult(random.nextFloat(200, 1000))
                .build();

        updateTransportDataInTransportModules(DataUpdateType.CREATE, newTransport);
    }

    private void updateRandomTransport() {
        Transport randomTransport = getRandomTransport();
        if (randomTransport == null) return;

        int currentGuestCapacity = randomTransport.getCapacity();
        int newCapacity = random.nextInt(0, (int) (currentGuestCapacity * random.nextDouble(0.1, 1.5)));

        float currentPricePerAdult = randomTransport.getPricePerAdult();
        float newPricePerAdult = random.nextFloat(currentPricePerAdult, currentPricePerAdult*10);

        randomTransport.setCapacity(newCapacity);
        randomTransport.setPricePerAdult(newPricePerAdult);

        updateTransportDataInTransportModules(DataUpdateType.UPDATE, randomTransport);
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
        TransportUpdateRequest transportUpdateRequest = TransportUpdateRequest.builder()
                .updateType(String.valueOf(updateType))
                .id(transport.getId())
                .courseId(transport.getCourse().getId())
                .departureDate(transport.getDepartureDate())
                .capacity(transport.getCapacity())
                .pricePerAdult(transport.getPricePerAdult())
                .build();

        String transportUpdateRequestJson = JsonConverter.convert(transportUpdateRequest);

        System.out.println(updateType + " - Transport: " + transportUpdateRequestJson);

        rabbitTemplate.convertAndSend(QueuesConfig.EXCHANGE_TRANSPORT_FANOUT_UPDATE_DATA, "", transportUpdateRequestJson);
    }
}
