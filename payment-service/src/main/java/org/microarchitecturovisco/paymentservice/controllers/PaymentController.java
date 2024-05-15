package org.microarchitecturovisco.paymentservice.controllers;

import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentResponseDto;
import org.microarchitecturovisco.paymentservice.services.PaymentService;
import org.microarchitecturovisco.paymentservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = "payments.requests.handle")
    public void consumePaymentRequest(String paymentRequestJson) {
        HandlePaymentRequestDto requestDto = JsonReader.handlePaymentRequestDtoFromJson(paymentRequestJson);

        HandlePaymentResponseDto responseDto = paymentService.verifyTransaction(requestDto);


    }
}
