package com.voice.yatraRegistration.memberReg.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="yatra_25_reg_mem",uniqueConstraints = { @UniqueConstraint(columnNames = { "upiTxnId" }) })
public class RegisteredMember {

    //detail
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    @Column(name="yatra_mem_id")
    private Long id;

    private String userEmail="";

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Member> memberIdList = new ArrayList<>();


    //transaction
    private String amount="";
    private String customerTxnId="";
    private String customerVPA="";
    private String customerEmail="";
    private String upiTxnId="";
    private String paymentStatus="";
    private String txnDate="";

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
}