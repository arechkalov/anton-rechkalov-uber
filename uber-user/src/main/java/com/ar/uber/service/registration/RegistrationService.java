package com.ar.uber.service.registration;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.uber.appuser.AppUser;
import com.ar.uber.appuser.AppUserRole;
import com.ar.uber.appuser.AppUserService;
import com.ar.uber.service.registration.token.ConfirmationToken;
import com.ar.uber.service.registration.token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final PhoneNumberValidator phoneNumberValidator;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {
        boolean isValidPhoneNumber = phoneNumberValidator.test(request.getPhoneNumber());

        if (!isValidPhoneNumber) {
            throw new IllegalStateException("phone number not valid");
        }

        return appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getPhoneNumber(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("phone number is already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getPhoneNumber());
        return "confirmed";
    }
}
