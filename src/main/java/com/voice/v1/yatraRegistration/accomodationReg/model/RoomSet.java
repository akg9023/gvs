package com.voice.v1.yatraRegistration.accomodationReg.model;

import java.util.List;


import jakarta.persistence.*;

import com.voice.v1.yatraRegistration.memberReg.model.Member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class RoomSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private RoomType roomType;

    @ManyToMany
    private List<Member> member;

    private String memCheckInTime;
    private String memCheckOutTime;

}