package cloud.project.datagenerator.transports.controllers;

import cloud.project.datagenerator.rabbitmq.json.JsonReader;
import cloud.project.datagenerator.rabbitmq.requests.transports.CreateTransportReservationRequest;
import cloud.project.datagenerator.rabbitmq.requests.transports.DeleteTransportReservationRequest;
import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportReservation;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.UUID;

@RequiredArgsConstructor
public class TransportsController {

    private final TransportRepository transportRepository;

    @RabbitListener(queues = "#{handleCreateTransportReservationQueue.name}")
    public void consumeMessageCreateTransportReservation(String requestDtoJson) {
        CreateTransportReservationRequest request = JsonReader.readCreateTransportReservationRequestCommand(requestDtoJson);

        System.out.println("Creating transport reservations: " + request);

        for (UUID transportId: request.getTransportIds()) {
            Transport transport = transportRepository.findById(transportId).orElse(null);;
            assert transport != null;

            TransportReservation reservation = TransportReservation.builder()
                    .id(UUID.randomUUID()) // random?
                    .mainReservationId(request.getReservationId())
                    .numberOfSeats(request.getAmountOfQuests())
                    .build();

            transport.getTransportReservations().add(reservation);
        }
    }

    @RabbitListener(queues = "#{handleDeleteTransportReservationQueue.name}")
    public void consumeMessageDeleteTransportReservation(String requestJson) {
        DeleteTransportReservationRequest request = JsonReader.readDeleteTransportReservationRequestCommand(requestJson);

        System.out.println("Deleting transport reservations: " + request);

        for (UUID transportId : request.getTransportReservationsIds()){
            Transport transport = transportRepository.findById(transportId).orElse(null);;
            assert transport != null;

            transport.getTransportReservations().removeIf(reservation -> reservation.getMainReservationId().equals(request.getReservationId()));
        }
    }

}
