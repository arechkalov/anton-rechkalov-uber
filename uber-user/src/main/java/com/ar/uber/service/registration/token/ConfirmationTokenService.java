package com.ar.uber.service.registration.token;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) { //rename to getConfirmationToken
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) { // rename to updateConfirmedAtConfirmationToken
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
