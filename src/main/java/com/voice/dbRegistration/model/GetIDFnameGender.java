package com.voice.dbRegistration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class GetIDFnameGender {
    private String id;
    private String fname;
    private Gender gender;
    @JsonIgnore
    private String dateOfBirth = "";
    private int age;
    public GetIDFnameGender(String id, String fname, Gender gender, String dateOfBirth) {
        this.id = id;
        this.fname = fname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge(){
        if(age!=0) return age;
        if(!dateOfBirth.isEmpty()){
            String dob = dateOfBirth;
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate birthDate = LocalDate.parse(dob, formatter);
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthDate, currentDate);
            age = period.getYears();
            return age;
        }
        return -1;
    }
}