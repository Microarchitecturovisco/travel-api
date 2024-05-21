package org.microarchitecturovisco.reservationservice.domain.exceptions;

public class PurchaseFailedException extends RuntimeException{
    public PurchaseFailedException() {
        super("Purchase failed");
    }

    public PurchaseFailedException(String message) {
        super("Purchase failed:" + message);
    }
}
