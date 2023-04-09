package com.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

@SpringBootApplication
public class HlzRegApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlzRegApplication.class, args);
	}

	

}
