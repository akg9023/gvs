package com.voice.registration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DevoteeInfoRequest {

    //Personal Info
    private String fname;
    private String mname;
    private String lname;
    private String initiatedName;
    private Gender gender;
    private String dob;  //Stringformat = <year>-<month>-<day> e.g "2020-09-08"
    private MaritialStatus maritialStatus;
    private AspiringAshram aspiringAshram; // null for married,brahmachari and sanyasi
    private String bloodGroup;
    private Language language;
    private String profileImgUrl;

    //Contact Info
    private String primaryPhone;
    private String whatsappPhone;
    private String email;
    private boolean isStayingInHaldiaVoice;
    private Address currentAddress; // null if staying in voice
    private Address permanentAddress;

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
    private Address officeAddress;

    //Family Info
    private String previousReligion;
    private String birthCity;
    private String birthState;
    private String motherTongue;
    private String fathersName;
    private String mothersName;

    //flags
    private boolean isParent; // true if he has dependents
    private boolean isModified; //true when any update happen and not committed to master db

}
