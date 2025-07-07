package com.voice.yatraRegistration.memberReg.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "getRegisteredMembers25",
                procedureName = "get_registered_members",
                resultClasses = Member25.class)
})
public class Member25 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String dbDevId;
    private String dbDevName;
    private String dbDevGender;
    private String dbDevAge;
}
