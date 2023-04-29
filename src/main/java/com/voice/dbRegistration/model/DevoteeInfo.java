package com.voice.dbRegistration.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@ToString
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "dateOfBirth", "primaryPhone", "fname" }) })
public class DevoteeInfo {

    // uniquely identification of every devotee
    // format - HLZ<day><month><year><hour><min><seconds> e.g HLZ200323122344
    @Id
    @GenericGenerator(name = "devotee_id", strategy = "com.voice.dbRegistration.utils.common.DevoteeIdGenerator")
    @GeneratedValue(generator = "devotee_id")
    @Column(name = "devotee_id")
    private String id;

    // Personal Info
    private String fname = "";
    private String mname = "";
    private String lname = "";
    private String initiatedName = "";
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.MALE;

    private String dateOfBirth = "";
    private String age=""; // calculated
    @Enumerated(EnumType.STRING)
    private MaritialStatus maritialStatus = MaritialStatus.UNMARRIED;

    @Enumerated(EnumType.STRING)
    private MaritialStatus maritalStatus = MaritialStatus.UNMARRIED;

    private String bloodGroup = "";
    private String language = "";
    private String profileImgUrl = ""; // PII (Personally Identifiable Information)

    // Contact Info
    private String primaryPhone=""; // PII
    private String whatsappPhone=""; // PII
    private String email=""; // PII

    @ManyToOne(cascade = CascadeType.ALL)
    private Address permanentAddress = new Address();

    @ManyToOne(cascade = CascadeType.ALL)
    private Address currentAddress = new Address();

    // Devotional Info
    private String facilitator=""; // to be fetched in UI from db
    private String spiritualMaster="";

    // Additional Devotional Info
    private String chantingRounds="";
    private String yearChantingSince=""; // must be four digit
    private String yearChanting16Rounds=""; // must be four digit
    private String introducedBy="";
    private String preferredServices="";
    private String servicesRendered="";

    // Professional Info
    @Enumerated(EnumType.STRING)
    private Education education= Education.NO_EDUCATION;
    private String degreeSpecification="";

    @Enumerated(EnumType.STRING)
    private Occupation occupation=Occupation.UNEMPLOYED;
    private String presentDesignation="";
    private String skills="";
    private String currentCompany="";
    private String officeLocation="";

    // Family Info
    private String motherTongue="";
    private String fathersName="";
    private String mothersName="";
    private String dateOfMarriage="";
    private String spouseName="";
    private String children="";

    // flags
    private String connectedTo="";//guru or else devoteeId
    private boolean isModified=false; // true when any update happen and not committed to master db

    // internal
    @Enumerated(EnumType.STRING)
    private Prividege priviledge=Prividege.USER;

    @Column(nullable = false, updatable = true)
    @CreationTimestamp
    private LocalDateTime createdDateTime;
 
    @Column(nullable = false, updatable = true)
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;

}
