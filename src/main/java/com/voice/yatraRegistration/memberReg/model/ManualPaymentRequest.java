package com.voice.yatraRegistration.memberReg.model;

import java.time.LocalDateTime;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ManualPaymentRequest{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userEmail;
    private String customerName;
    private String customerPhoneNo;
    private String customerUTR;
    private String customerUPIApp;
    private String amount;
    //flags
    private Boolean isApproved = false;
    private String comments;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
}