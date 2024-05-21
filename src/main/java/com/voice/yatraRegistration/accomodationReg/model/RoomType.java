package com.voice.yatraRegistration.accomodationReg.model;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class RoomType {

    @Id
    private String roomId;

    private String type;
    private String description;
    private String price;
    private Integer count;
    private Integer memberCount;
    private Integer minMemberCount;
    private String checkInTime; //24Hrs
    private String checkOutTime; //24Hrs


    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;

}