package com.ar.uber.service.registration;

import java.util.function.Predicate;

import org.springframework.stereotype.Service;

@Service
public class PhoneNumberValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
//        TODO: Regex to validate phone number
        return true;
    }
}
