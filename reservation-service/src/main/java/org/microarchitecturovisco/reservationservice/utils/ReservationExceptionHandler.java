package org.microarchitecturovisco.reservationservice.utils;

import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationNotFoundAfterPaymentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ReservationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ReservationNotFoundAfterPaymentException.class)
    protected ResponseEntity handleInternalServerError(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ResponseEntity.internalServerError().body(ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);

    }
}
