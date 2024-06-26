package org.microarchitecturovisco.paymentservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentResponseDto;
import org.microarchitecturovisco.paymentservice.services.PaymentService;
import org.microarchitecturovisco.paymentservice.utils.JsonConverter;
import org.microarchitecturovisco.paymentservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @RabbitListener(queues = "payments.requests.handle")
    public String consumePaymentRequest(String paymentRequestJson) {
        Logger logger = Logger.getLogger("Payment request");

        logger.info("Request: " + paymentRequestJson);

        HandlePaymentRequestDto requestDto = JsonReader.handlePaymentRequestDtoFromJson(paymentRequestJson);

        HandlePaymentResponseDto responseDto = paymentService.verifyTransaction(requestDto);

        logger.info("Response: " + responseDto);

        return JsonConverter.convertHandlePaymentResponseDto(responseDto);
    }
}
