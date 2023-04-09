package com.voice.dbRegistration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Getter
@Setter
@ToString
public class DevoteeInfo {

    // uniquely identification of every devotee
    // format - HLZ<day><month><year><hour><min><seconds> e.g HLZ200323122344
    @Id
    @GenericGenerator(name = "devotee_id", strategy = "com.voice.dbRegistration.utils.common.MyGenerator")
    @GeneratedValue(generator = "devotee_id")  
    @Column(name="devotee_id")
    private String id;

    //Personal Info
    private String fname;
    private String mname;
    private String lname;
    private String initiatedName;
    private Gender gender;
    //dob
    private String dob;
    //dob-end
    private MaritialStatus maritialStatus;
    private AspiringAshram aspiringAshram; 
    private String bloodGroup;
    private Language language;
    private String profileImgUrl; //PII (Personally Identifiable Information)

    //Contact Info
    private String primaryPhone; //PII
    private String whatsappPhone; //PII
    private String email; //PII
    private boolean isStayingInHaldiaVoice;

    //homeString
    private String homeLine1;
    private String homeLine2;
    private String homeCity;
    private String homeState;
    private String homePincode;
    private String homeCountry;

     //currentString
     private String currLine1;
     private String currLine2;
     private String currCity;
     private String currState;
     private String currPincode;
     private String currCountry;


    //Devotional Info
    private String centerConnectedTo;
    private String facilitator; //to be fetched in UI from db
    private String counselor;
    private String spiritualMaster;

    //Additional Devotional Info
    private String chantingRounds;
    private String yearChantingSince; //must be four digit
    private String yearChanting16Rounds; //must be four digit
    private String introducedBy;
    private String yearOfIntroduction; //must be four digit
    private String placeIntroducedIn;
    private String previousCounselor;
    private String preferredServices;
    private String servicesRendered;
    private String remarks;

    //Professional Info
    private Education education;
    private Occupation occupation;
    private String presentDesignation;
    private String awards;
    private String skills;
    private String currentCompany;

    //Family Info
    private String previousReligion;
    private String birthCity;
    private String birthState;
    private String motherTongue;
    private String fathersName;
    private String mothersName;

    //flags
    private String connectedTo;
    private boolean isModified; //true when any update happen and not committed to master db

}
