package org.microarchitecturovisco.paymentservice;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.utils.JsonConverter;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final RabbitTemplate paymentRabbitTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {

    }

    @Bean
    public DirectExchange testDirectExchange() {
        return new DirectExchange("payments.responses.handle");
    }


    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void testPayment() {

        String response = (String) paymentRabbitTemplate.convertSendAndReceive("payments.requests.handle", "payments.handlePayment", JsonConverter.convertHandlePaymentRequestDto(
                HandlePaymentRequestDto.builder()
                        .id(UUID.randomUUID())
                        .cardNumber("1234567812345678")
                        .idReservation(UUID.randomUUID())
                        .build()
        ));
    }
}
