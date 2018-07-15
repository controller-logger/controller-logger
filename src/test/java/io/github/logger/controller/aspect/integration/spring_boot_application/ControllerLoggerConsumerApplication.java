package io.github.logger.controller.aspect.integration.spring_boot_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControllerLoggerConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControllerLoggerConsumerApplication.class, args);
	}
}