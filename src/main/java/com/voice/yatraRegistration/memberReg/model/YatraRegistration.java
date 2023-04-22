package com.voice.yatraRegistration.memberReg.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// @Entity
@Getter
@Setter
@ToString
@Table(name="yatra_aug_23")
public class YatraRegistration {
    
    @Id
    @GenericGenerator(name = "yatra_id", strategy = "com.voice.yatraRegistration.memberReg.utils.common.YatraMemRegIdGenerator")
    @GeneratedValue(generator = "yatra_id")  
    @Column(name="yatra_mem_id")
    private String id;

    //Member details
    private List<Member> memberIdList = new ArrayList<>();

    //Accomodation details


    
}
