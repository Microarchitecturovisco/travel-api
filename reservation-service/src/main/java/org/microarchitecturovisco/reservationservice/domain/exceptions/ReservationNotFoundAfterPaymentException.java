package org.microarchitecturovisco.reservationservice.domain.exceptions;

public class ReservationNotFoundAfterPaymentException extends RuntimeException {
    public ReservationNotFoundAfterPaymentException() {
        super("Reservation was paid successfully, but the system could not find it");
    }
}
