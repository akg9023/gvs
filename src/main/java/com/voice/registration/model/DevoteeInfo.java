package com.voice.registration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

enum Gender{
    MALE,
    FEMALE
}

enum MaritialStatus{
    UNMARRIED,
    MARRIED,
    DIVORCED,
    BRAHMACHARI,
    SANYASI
}

enum AspiringAshram{
    BRAHMACHARI,
    GRIHASTHA,
    NOT_DECIDED,
    NULL // for married,brahmachari and sanyasi
}

enum Language{
    BENGALI,
    ENGLISH,
    HINDI,
    GUJARATI,
    KANNADA,
    MARATHI,
    MAITHILI,
    ORIYA,
    NEPALI,
    PUNJABI
}

enum Education{
    NO_EDUCATION,
    PRE_PRIMARY_SCHOOL,
    PRIMARY_SCHOOL,
    MIDDLE_SCHOOL,
    SECONDARY_SCHOOL,
    HIGHER_SECONDARY_SCHOOL,
    DIPLOMA,
    UG,
    PG,
    DOCTORATE,
    POST_DOCTORATE
}

enum Occupation{
    EMPLOYEED_FULL_TIME,
    EMPLOYEED_PART_TIME,
    SELF_EMPLOYED,
    UNEMPLOYED,
    HOMEMAKER,
    RETIRED,
    STUDENT
}

@Getter
@Setter
@ToString
public class DevoteeInfo {
    //Personal Info
    private String fname;
    private String mname;
    private String lname;
    private String initiatedName;
    private Gender gender;
    private Date dob;
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
    private boolean isApproved;
        //handled in storing data to master db
    private boolean isModified; //true when any update happen and not committed to master db

}
