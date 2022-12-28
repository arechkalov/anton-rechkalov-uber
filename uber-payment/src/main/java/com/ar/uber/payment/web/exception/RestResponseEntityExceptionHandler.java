package com.ar.uber.payment.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String FAILED = "FAILED";

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GenericResponse> validationException(ValidationException validationException) {
        log.info(validationException.getMessage(), validationException.getCause());
        return response(HttpStatus.BAD_REQUEST, validationException.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GenericResponse> debtorNotFoundException(EntityNotFoundException entityNotFoundException) {
        log.info(entityNotFoundException.getMessage(), entityNotFoundException.getCause());
        return response(HttpStatus.BAD_REQUEST, entityNotFoundException.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<GenericResponse> illegalStateException(IllegalStateException illegalStateException) {
        log.info(illegalStateException.getMessage(), illegalStateException.getCause());
        return response(HttpStatus.BAD_REQUEST, illegalStateException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> genericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error has occured");
    }

    public static <T> ResponseEntity<GenericResponse> response(final HttpStatus httpStatus, final String message) {
        GenericResponse response = GenericResponse.builder()
                .status(FAILED)
                .message(message)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

}
