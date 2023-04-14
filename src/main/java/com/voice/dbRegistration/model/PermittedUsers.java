package com.voice.dbRegistration.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class PermittedUsers {
    @Id
    @GeneratedValue(strategy =GenerationType.SEQUENCE)
    private Long id;

    String email;
}
