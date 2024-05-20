package com.voice.v1.dbRegistration.restController;

import java.time.LocalDate;
import java.time.Period;

public class Helper {

    public static String calculateAge(String dob) {

        LocalDate local = LocalDate.parse(dob);

		// creating an instance of the LocalDate class and invoking the now() method
		// now() method obtains the current date from the system clock in the default
		// time zone
		LocalDate curDate = LocalDate.now();
		// calculates the amount of time between two dates and returns the years
		if ((dob != null) && (curDate != null)) {
			int ageInt = Period.between(local, curDate).getYears();
            return String.valueOf(ageInt);
		} else {
			return "0";
		}

	}

}