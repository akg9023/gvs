package com.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.dbRegistration.model.PermittedUsers;
import com.voice.dbRegistration.restController.Helper;
import com.voice.dbRegistration.restController.PermittedUsersRestController;
import com.voice.dbRegistration.service.SendSmsService;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
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
