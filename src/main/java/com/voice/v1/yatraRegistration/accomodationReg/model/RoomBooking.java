package com.voice.v1.yatraRegistration.accomodationReg.model;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


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