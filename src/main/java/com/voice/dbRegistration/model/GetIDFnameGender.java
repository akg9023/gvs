package com.voice.dbRegistration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class GetIDFnameGender {
    private String id;
    private String fname;
    private Gender gender;
    private String age;
}