package com.ar.uber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@PropertySource("classpath:exception.properties")
@EnableRetry(proxyTargetClass = true)
public class UberUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberUserApplication.class, args);
	}
}
