package com.voice.v1.yatraRegistration.memberReg.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="backup_yatra_aug_23_reg_mem")
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