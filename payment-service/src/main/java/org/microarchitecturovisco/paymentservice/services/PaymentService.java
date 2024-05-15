package org.microarchitecturovisco.paymentservice.services;

import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentRequestDto;
import org.microarchitecturovisco.paymentservice.models.dto.HandlePaymentResponseDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public HandlePaymentResponseDto verifyTransaction(HandlePaymentRequestDto requestDto) {
        boolean transactionApproved = Integer.parseInt(requestDto.getCardNumber()) % 2 == 0;

        return HandlePaymentResponseDto.builder()
                .id(requestDto.getId())
                .transactionApproved(transactionApproved)
                .build();
    }
}
