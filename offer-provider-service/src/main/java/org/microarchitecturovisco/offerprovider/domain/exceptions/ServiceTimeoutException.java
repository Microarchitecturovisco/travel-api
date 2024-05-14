package org.microarchitecturovisco.offerprovider.domain.exceptions;

public class ServiceTimeoutException extends RuntimeException{
    public ServiceTimeoutException() {
        super("Other service timed out");
    }
}
