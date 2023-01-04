package com.ar.uber.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ar.uber.appuser.AppUserService;
import com.ar.uber.security.JwtUtil;
import com.ar.uber.service.registration.AuthenticationRequest;
import com.ar.uber.service.registration.AuthenticationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1")
// Add swagger and java documentations for all public methods
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final JwtUtil jwtTokenUtil;

    @RequestMapping(value = "authentication", method = RequestMethod.POST) // replace with RequestMapping.POST.
    // rename AuthenticationRequest => CreateAuthenticationRequest
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception { // create wrap and return CreateAuthenticationTokenResponse as return type add annotation ResponseStatus Ok.

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getPhoneNumber(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e); //remove try/catch block and catch exception in ControllerAdvice, or rethrow dedicated custom exception and handle it in ControllerAdvice.
        }

        final UserDetails userDetails = appUserService.loadUserByUsername(authenticationRequest.getPhoneNumber());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
