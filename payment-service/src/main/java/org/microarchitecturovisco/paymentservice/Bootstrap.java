package org.microarchitecturovisco.paymentservice;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.utils.JsonConverter;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void testPayment() {

        UUID testId = UUID.randomUUID();

        System.out.println("Agent 1 Sending request with uuid " + testId);

        String response = (String) rabbitTemplate.convertSendAndReceive(exchange.getName(), "payments.handlePayment", JsonConverter.convertHandlePaymentRequestDto(
                HandlePaymentRequestDto.builder()
                        .cardNumber("1234567812345678")
                        .idReservation(UUID.randomUUID().toString())
                        .build()
        ));

        System.out.println("Agent 1 Received response " + response);
    }

    @Scheduled(fixedRate = 5000, initialDelay = 10000)
    public void testPayment2() {

        UUID testId = UUID.randomUUID();

        System.out.println("Agent 2 Sending request with uuid " + testId);

        String response = (String) rabbitTemplate.convertSendAndReceive(exchange.getName(), "payments.handlePayment", JsonConverter.convertHandlePaymentRequestDto(
                HandlePaymentRequestDto.builder()
                        .cardNumber("1234567812345677")
                        .idReservation(UUID.randomUUID().toString())
                        .build()
        ));

        System.out.println("Agent 2 Received response " + response);
    }
}
