package com.ar.uber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@PropertySource("classpath:exception.properties")
@EnableRetry(proxyTargetClass = true)
//add javadoc for all public contract/interface methods (make it for the whole project)
// add unit, e2e and integration tests.
// use value objects instead of primitives in all request and response entities.
public class UberUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberUserApplication.class, args);
	}
}
