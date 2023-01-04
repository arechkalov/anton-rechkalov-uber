package com.ar.uber.web.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.uber.service.registration.RegistrationRequest;
import com.ar.uber.service.registration.RegistrationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
// Add swagger and java documentations for all public methods
//Use @ControllerAdvice and create custom ApiError.class and Http.Status response for negative scenarios such as EntityNotFound, UserNotFound etc. exceptions.
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    //rename RegistrationRequest => RegisterRequest
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) { // wrap return type into RegistrationResponse with value object of created URI.
        return ResponseEntity.created(URI.create(registrationService.register(request))).build();
    }

    @GetMapping(path = "confirmation") // Use put mapping instead of get, make method void and use annotation @ResponseStatus with OK instead of returning ResponseEntity.
    public ResponseEntity<String> confirm(@RequestParam("token") String token) { // wrap token into ConfirmRegistrationRequest and wrap into it value object RegistrationToken
        return ResponseEntity.ok(registrationService.confirmToken(token));
    }

}
