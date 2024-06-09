package cloud.project.datagenerator.transports;

import cloud.project.datagenerator.hotels.domain.Hotel;
import cloud.project.datagenerator.hotels.repositories.HotelRepository;
import cloud.project.datagenerator.rabbitmq.QueuesConfigTransports;
import cloud.project.datagenerator.rabbitmq.json.JsonConverter;
import cloud.project.datagenerator.rabbitmq.requests.transports.TransportUpdateRequest;
import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportCourse;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import cloud.project.datagenerator.transports.utils.TransportUtils;
import cloud.project.datagenerator.websockets.transports.DataGeneratorTransportsWebSocketHandler;
import cloud.project.datagenerator.websockets.transports.TransportUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TransportsDataGenerator {
    private final HotelRepository hotelRepository;

    enum DataUpdateType {
        CREATE,
        UPDATE
    }

    private final Random random = new Random();
    private final RabbitTemplate rabbitTemplate;
    private final DataGeneratorTransportsWebSocketHandler dataGeneratorTransportsWebSocketHandler;
    private final Logger logger = Logger.getLogger("DataGenerator | Transports");
    private final TransportUtils transportUtils;
    private final TransportRepository transportRepository;

    @Scheduled(fixedDelay = 5000, initialDelay = 12500)
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
        TransportCourse randomTransportCourse = transportUtils.getRandomTransportCourse();
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

        transportRepository.save(newTransport);

        updateTransportDataInTransportModules(DataUpdateType.CREATE, newTransport);

        updateTransportUpdatesOnFrontend(DataUpdateType.CREATE, newTransport, 0, 0);
    }

    private void updateRandomTransport() {
        Transport randomTransport = transportUtils.getRandomTransport();
        if (randomTransport == null) return;

        int currentGuestCapacity = randomTransport.getCapacity();
        int newGuestCapacity = random.nextInt(currentGuestCapacity, currentGuestCapacity + 10);

        float currentPricePerAdult = randomTransport.getPricePerAdult();
        float newPricePerAdult = random.nextFloat(100, currentPricePerAdult + 100);

        int capacityChange = newGuestCapacity - currentGuestCapacity;
        float priceChange = newPricePerAdult - currentPricePerAdult;

        randomTransport.setCapacity(newGuestCapacity);
        randomTransport.setPricePerAdult(newPricePerAdult);

        transportRepository.save(randomTransport);

        updateTransportDataInTransportModules(DataUpdateType.UPDATE, randomTransport);

        updateTransportUpdatesOnFrontend(DataUpdateType.UPDATE, randomTransport, capacityChange, priceChange);
    }

    public void updateTransportDataInTransportModules(DataUpdateType updateType, Transport transport) {
        TransportUpdateRequest transportUpdateRequest = TransportUpdateRequest.builder()
                .updateType(String.valueOf(updateType))
                .id(transport.getId())
                .courseId(transport.getCourse().getId())
                .courseLocationFromId(transport.getCourse().getDepartureFrom().getId())
                .courseLocationToId(transport.getCourse().getArrivalAt().getId())
                .departureDate(transport.getDepartureDate())
                .capacity(transport.getCapacity())
                .pricePerAdult(transport.getPricePerAdult())
                .build();

        String transportUpdateRequestJson = JsonConverter.convert(transportUpdateRequest);

        logger.info(updateType + " - Transport: " + transportUpdateRequestJson);

        rabbitTemplate.convertAndSend(QueuesConfigTransports.EXCHANGE_TRANSPORT_FANOUT_UPDATE_DATA, "", transportUpdateRequestJson);
    }

    private void updateTransportUpdatesOnFrontend(DataUpdateType updateType, Transport transport, int capacityChange, float priceChange) {
        LocalDateTime currentDateAndTime = LocalDateTime.now().withNano(0);

        String departureRegion = transport.getCourse().getDepartureFrom().getRegion();
        String departureCountry = transport.getCourse().getDepartureFrom().getCountry();
        String arrivalRegion = transport.getCourse().getArrivalAt().getRegion();
        String arrivalCountry = transport.getCourse().getArrivalAt().getCountry();
        String transportTypeName = transport.getCourse().getType().toString();

        TransportUpdate transportUpdate = TransportUpdate.builder()
                .updateDateTime(currentDateAndTime.toString())
                .updateType(String.valueOf(updateType))
                .departureRegionAndCountry(departureRegion + ", " + departureCountry)
                .arrivalRegionAndCountry(arrivalRegion + ", " + arrivalCountry)
                .transportTypeName(transportTypeName)
                .priceChange(priceChange)
                .capacityChange(capacityChange)
                .build();

        dataGeneratorTransportsWebSocketHandler.updateTransportList(transportUpdate);
    }
}
