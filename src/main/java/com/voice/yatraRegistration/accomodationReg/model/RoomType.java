package com.voice.yatraRegistration.accomodationReg.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    // @Enumerated(EnumType.STRING)
    private RType type;
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

enum RType{
    AC,
    NONAC
} 
