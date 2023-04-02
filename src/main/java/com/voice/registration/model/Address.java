package com.voice.registration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Address {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name="devotee_id",nullable = false)
    private DevoteeInfoResponse devoteeInfoResponse;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private String addressType;
}
