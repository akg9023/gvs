package com.voice.registration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Getter
@Setter
@ToString
public class DevoteeInfoResponse {

    // uniquely identification of every devotee
    // format - HLZ<day><month><year><hour><min><seconds> e.g HLZ200323122344
    @Id
    @Column(name="devotee_id")
    private Long id;

    //Personal Info
    private String fname;
    private String mname;
    private String lname;
    private String initiatedName;
    private Gender gender;
    //dob
    private String dobDay;
    private String dobMonth;
    private String dobYear;
    //dob-end
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

    
    @OneToMany(mappedBy = "devoteeInfoResponse")
    private Set<Address> addresses; // null if staying in voice

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

    // login
    private String parentLoginPassword; // null if not parent

    //flags
    private boolean isParent; // true if he has dependents
    private boolean isModified; //true when any update happen and not committed to master db

}
