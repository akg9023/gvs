package com.voice.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class YatraRegEmail {
    //Logger logger = LoggerFactory.getLogger(YatraRegEmail.class);
    @Getter(AccessLevel.NONE)
    private Long yatraMemId;
    //who is filling the form
    private String userId;
    private String userFname;
    private String userLname;
    private String userEmail="";
    private String gender;
    private String memberId;
    private String fname;
    private String lname;

    private String dateOfBirth;

    private String primaryPhone;
    private String city;
    @Getter(AccessLevel.NONE)
    private int calculatedAge;

    public YatraRegEmail() {
    }

    public YatraRegEmail(String userId, String userFname, String userLname, String userEmail, String gender, String memberId, String fname, String lname, String dateOfBirth, String primaryPhone, String city) {
        this.userId = userId;
        this.userFname = userFname;
        this.userLname = userLname;
        this.userEmail = userEmail;
        this.gender = gender;
        this.memberId = memberId;
        this.fname = fname;
        this.lname = lname;
        this.dateOfBirth = dateOfBirth;
        this.primaryPhone = primaryPhone;
        this.city = city;
    }

    public int getCalculatedAge(){
        try {
            if (calculatedAge != 0) return calculatedAge;
            if (!dateOfBirth.isEmpty()) {
                String dob = dateOfBirth;
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDate birthDate = LocalDate.parse(dob, formatter);
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(birthDate, currentDate);
                calculatedAge = period.getYears();
                return calculatedAge;
            }
        }catch (Exception e){
            //logger.error("Get Calculated age error {}",e.getMessage());
        }
        return -1;
    }
}