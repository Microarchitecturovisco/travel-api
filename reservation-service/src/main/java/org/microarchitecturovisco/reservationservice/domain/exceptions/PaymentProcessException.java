package org.microarchitecturovisco.reservationservice.domain.exceptions;

public class PaymentProcessException extends Exception{
    public PaymentProcessException() {
        super("Payment process failed");
    }

    public PaymentProcessException(String message) {
        super(message);
    }
}
