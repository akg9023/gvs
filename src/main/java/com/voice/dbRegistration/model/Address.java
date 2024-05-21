package com.voice.dbRegistration.model;



import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Address {

    @Id
    // @GenericGenerator(name = "address_id", strategy = "com.voice.dbRegistration.utils.common.AddressIdGenerator")
    // @GeneratedValue(generator = "address_id")
    @Column(name="address_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}