package com.voice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//import com.voice.dbRegistration.service.SendSmsService;

import java.util.TimeZone;

@SpringBootApplication
// @EnableScheduling
@EnableAsync
public class HlzRegApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlzRegApplication.class, args);


	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
	}

}