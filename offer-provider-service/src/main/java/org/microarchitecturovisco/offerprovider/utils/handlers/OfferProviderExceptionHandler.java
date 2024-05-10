package org.microarchitecturovisco.offerprovider.utils.handlers;

import org.microarchitecturovisco.offerprovider.domain.exceptions.ServiceTimeoutException;
import org.microarchitecturovisco.offerprovider.domain.exceptions.WrongDateFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OfferProviderExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(
            WrongDateFormatException.class
    )
    protected ResponseEntity handleBadRequest(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ResponseEntity.badRequest().body(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(
            ServiceTimeoutException.class
    )
    protected ResponseEntity handleInternalServerError(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ResponseEntity.internalServerError().body(ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
