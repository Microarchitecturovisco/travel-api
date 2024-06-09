package org.microarchitecturovisco.hotelservice.model.exceptions;

public class HotelNoFoundException extends Exception {
    public HotelNoFoundException() {
        super("Hotel with given id was not found");
    }
}
