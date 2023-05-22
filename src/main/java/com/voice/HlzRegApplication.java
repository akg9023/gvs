package com.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.common.utils.SendSmsService;
import com.voice.dbRegistration.model.PermittedUsers;
import com.voice.dbRegistration.restController.Helper;
import com.voice.dbRegistration.restController.PermittedUsersRestController;

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
public class HlzRegApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlzRegApplication.class, args);

		// BufferedInputStream file = new BufferedInputStream(new
		// FileInputStream("C:\\Users\\mahad\\Downloads\\tmpcaa023726oda.rpt"));
		// //TODO: for multipartfile received from web, send
		// multipartfile.getInputStream() as parameter
		// ExtractBankTransactions extractBankTransactions = new
		// ExtractBankTransactions(file);
		// //TODO: call extractTransaction method with transaction id to get transaction
		// object.
		// // May use matches method in Transaction class for matching transactions.
		// // Note that extractTransaction method returns null if matching transaction
		// is not found.
		// Transaction t1 = extractBankTransactions.extractTransaction("236435933480");
		// Transaction t2 = extractBankTransactions.extractTransaction("313105960142");
		// System.out.println(t1);
		// System.out.println(t2);
		// System.out.println(extractBankTransactions.getLastDate());
	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
	}

}
