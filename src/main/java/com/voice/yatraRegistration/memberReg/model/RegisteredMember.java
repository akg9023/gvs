package com.voice.yatraRegistration.memberReg.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="yatra_aug_23")
public class RegisteredMember {
    
    //detail
    @Id
    @GenericGenerator(name = "yatra_id", strategy = "com.voice.yatraRegistration.memberReg.utils.common.YatraMemRegIdGenerator")
    @GeneratedValue(generator = "yatra_id")  
    @Column(name="yatra_mem_id")
    private String id;

    //who is filling the form
    private String userEmail;   

    @OneToMany(cascade = CascadeType.ALL)
    private List<Member> memberIdList = new ArrayList<>();

    //transaction
    private String amount;
    private String customerTxnId;
    private String customerVPA;
    private String customerEmail;
    private String upiTxnId;
    private String paymentStatus;
    private String txnDate;
}
