package com.voice.yatraRegistration.memberReg.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="backup_yatra_aug_24_reg_mem")
public class BackupRegisteredMember{

    //detail
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="yatra_mem_id")
    private Long id;

    //who is filling the form
    private String userEmail="";
    private String memberIdList ="";

    //transaction
    private String amount="";
    private String customerEmail="";
    private String customerTxnId="";
    private String paymentStatus="";
    private LocalDateTime attemptDateTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

}