package com.ar.uber.web.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.ar.uber.web.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${exception.internalErrorMessage}")
    private String internalErrorMessage;

    @Value("${exception.tripAlreadyAcquired}")
    private String tripAlreadyAcquired;

    @Value("${exception.tripAlreadyFinished}")
    private String tripAlreadyFinished;

    @Value("${exception.usernameNotFoundException}")
    private String usernameNotFoundException;

    @Value("${exception.entityNotFoundException}")
    private String entityNotFoundException;

    @Value("${exception.tripNotFoundForCurrentUserException} ")
    private String tripNotFoundForCurrentUserException;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> genericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return internalServerError(internalErrorMessage);
    }

    @ExceptionHandler(TripAlreadyAcquiredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> tripAlreadyAcquiredExceptionHandler() {
        return new ResponseEntity<>(tripAlreadyAcquired, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TripAlreadyFinishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> tripAlreadyFinishedExceptionHandler() {
        return new ResponseEntity<>(tripAlreadyFinished, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> userNotFoundExceptionHandler() {
        return new ResponseEntity<>(usernameNotFoundException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> entityNotFoundExceptionHandler() {
        return new ResponseEntity<>(entityNotFoundException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TripNotFoundForCurrentUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> tripNotFoundForCurrentUserException() {
        return new ResponseEntity<>(tripNotFoundForCurrentUserException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> validationException(ValidationException validationException) {
        return new ResponseEntity<>(validationException.getMessage(), HttpStatus.NOT_FOUND);
    }

    private static ResponseEntity<GenericResponse> internalServerError(final Object body) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(GenericResponse.builder()
                .code(INTERNAL_SERVER_ERROR.value())
                .body(body)
                .build());
    }

}
