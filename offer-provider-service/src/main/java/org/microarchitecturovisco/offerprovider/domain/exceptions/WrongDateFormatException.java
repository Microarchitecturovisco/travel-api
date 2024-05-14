package org.microarchitecturovisco.offerprovider.domain.exceptions;

public class WrongDateFormatException extends RuntimeException{
    public WrongDateFormatException() {
        super("Given date string is in wrong format. Correct format: yyyy-MM-dd");
    }
}
