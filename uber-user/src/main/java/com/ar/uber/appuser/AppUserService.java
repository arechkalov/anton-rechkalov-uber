package com.ar.uber.appuser;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ar.uber.model.Driver;
import com.ar.uber.service.registration.token.ConfirmationToken;
import com.ar.uber.service.registration.token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with phone number %s not found"; // public static final

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber)
            throws UsernameNotFoundException {
        return appUserRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, phoneNumber)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByPhoneNumber(appUser.getPhoneNumber())
                .isPresent();

        if (userExists) {
            throw new IllegalStateException("phone number is already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);
        AppUser savedUser = appUserRepository.save(appUser);
        Driver driver = Driver.builder()
                .id(savedUser.getId())
                .appUser(savedUser)
                .build();
        savedUser.setDriver(driver);

        String token = UUID.randomUUID().toString(); //TODO ADD jwt

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

        return token;
    }

    public int enableAppUser(String phoneNumber) {
        return appUserRepository.enableAppUser(phoneNumber);
    }
}
