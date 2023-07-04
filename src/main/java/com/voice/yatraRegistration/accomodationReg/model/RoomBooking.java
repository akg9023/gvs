package com.voice.yatraRegistration.accomodationReg.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.yatraRegistration.memberReg.model.Member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "upiTxnId" }) })
public class RoomBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<RoomSet> roomSet;

   //transaction
    private String amount="";
    private String customerTxnId="";
    private String customerVPA="";
    private String customerEmail="";
    private String customerName="";
    private String customerPhoneNo="";
    private String upiTxnId;
    private String paymentStatus="";
    private String txnDate="";
    
    @Column( updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;
 
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
    
}

