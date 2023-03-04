package com.voice.registration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
