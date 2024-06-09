package cloud.project.datagenerator.transports.controllers;

import cloud.project.datagenerator.rabbitmq.json.JsonReader;
import cloud.project.datagenerator.rabbitmq.requests.transports.CreateTransportReservationRequest;
import cloud.project.datagenerator.rabbitmq.requests.transports.DeleteTransportReservationRequest;
import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportReservation;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import cloud.project.datagenerator.transports.repositories.TransportReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController()
@RequiredArgsConstructor
public class TransportsController {

    private final TransportRepository transportRepository;
    private final TransportReservationRepository transportReservationRepository;
    Logger logger = Logger.getLogger("DataGenerator | Hotels");

    @RabbitListener(queues = "#{handleCreateTransportReservationQueue.name}")
    public void consumeMessageCreateTransportReservation(String requestDtoJson) {
        CreateTransportReservationRequest request = JsonReader.readCreateTransportReservationRequestCommand(requestDtoJson);

        logger.info("Creating transport reservations: " + request);

        for (UUID transportId : request.getTransportIds()) {
            Transport transport = transportRepository.findById(transportId).orElse(null);
            assert transport != null;

            int countBefore = transport.getTransportReservations().size();

            TransportReservation reservation = TransportReservation.builder()
                    .id(UUID.randomUUID())
                    .mainReservationId(request.getReservationId())
                    .numberOfSeats(request.getAmountOfQuests())
                    .transport(transport)
                    .build();

            transport.getTransportReservations().add(reservation);
            transportReservationRepository.save(reservation);

            int countAfter = transport.getTransportReservations().size();
            logger.info("consumeMessageCreateTransportReservation: " + countBefore + " --> " + countAfter);
        }
    }

    @RabbitListener(queues = "#{handleDeleteTransportReservationQueue.name}")
    public void consumeMessageDeleteTransportReservation(String requestJson) {
        DeleteTransportReservationRequest request = JsonReader.readDeleteTransportReservationRequestCommand(requestJson);

        logger.info("Deleting transport reservations: " + request);

        for (UUID transportId : request.getTransportReservationsIds()) {
            Transport transport = transportRepository.findById(transportId).orElse(null);
            assert transport != null;

            int countBefore = transport.getTransportReservations().size();

            List<TransportReservation> reservationsToRemove = new ArrayList<>();
            for (TransportReservation reservation : transport.getTransportReservations()) {
                if (reservation.getMainReservationId().equals(request.getReservationId())) {
                    reservationsToRemove.add(reservation);
                }
            }
            transport.getTransportReservations().removeAll(reservationsToRemove);

            int countAfter = transport.getTransportReservations().size();
            logger.info("consumeMessageDeleteTransportReservation: " + countBefore + " --> " + countAfter);
        }
    }

}
