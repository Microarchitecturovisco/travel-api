package org.microarchitecturovisco.reservationservice;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.dto.HandleReservationRequestDto;
import org.microarchitecturovisco.reservationservice.utils.JsonConverter;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    @Override
    public void run(String... args) {}

    private final DirectExchange exchange;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void testReservation() {

        UUID testId = UUID.randomUUID();

        System.out.println("Agent 1 Sending request with uuid " + testId);

        String response = (String) rabbitTemplate.convertSendAndReceive(exchange.getName(),
                "reservations.handleReservation",
                JsonConverter.convertHandleReservationRequestDto(
                HandleReservationRequestDto.builder()
                        .id(testId)
                        .cardNumber("1234567812345678")
                        .idReservation(UUID.randomUUID())
                        .build()
        ));

        System.out.println("Agent 1 Received response " + response);
    }

}
