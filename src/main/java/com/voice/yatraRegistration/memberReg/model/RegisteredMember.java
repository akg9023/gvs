package com.voice.yatraRegistration.memberReg.model;

import java.util.List;

import javax.persistence.CascadeType;
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
    private String yatraMemRegId;

    //who is filling the form
    private String userId;

    @OneToMany(mappedBy = "registeredMember",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Member> memberIdList;

    //transaction
    private String amount;
    private String transactionId;
    private String upiId;

}
