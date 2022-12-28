package com.ar.uber.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry(proxyTargetClass=true)
public class UberPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberPaymentApplication.class, args);
	}
}
