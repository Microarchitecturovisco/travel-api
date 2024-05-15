package org.microarchitecturovisco.paymentservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentResponseDto;
import org.microarchitecturovisco.paymentservice.services.PaymentService;
import org.microarchitecturovisco.paymentservice.utils.JsonConverter;
import org.microarchitecturovisco.paymentservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "payments.requests.handle")
    @SendTo("payments.responses.handle")
    public String consumePaymentRequest(String paymentRequestJson) {
        HandlePaymentRequestDto requestDto = JsonReader.handlePaymentRequestDtoFromJson(paymentRequestJson);

        HandlePaymentResponseDto responseDto = paymentService.verifyTransaction(requestDto);

        return JsonConverter.convertHandlePaymentResponseDto(responseDto);
    }
}
