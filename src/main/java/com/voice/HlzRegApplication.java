package com.voice;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//import com.voice.dbRegistration.service.SendSmsService;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class HlzRegApplication {
    private static final Logger log = LoggerFactory.getLogger(HlzRegApplication.class);

    public static void main(String[] args) {
        log.info("Starting GVS application");
        SpringApplication.run(HlzRegApplication.class, args);

    }

    @PostConstruct
    public void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("IST"));
    }

}