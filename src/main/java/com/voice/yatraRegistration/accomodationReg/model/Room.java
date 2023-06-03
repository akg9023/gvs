package com.voice.yatraRegistration.accomodationReg.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private RoomType type;
    private String description;
    private String price;
    private Integer availability;


    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;
 
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
    
}

enum RoomType{
    AC,
    NONAC
} 
