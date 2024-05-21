package com.voice.yatraRegistration.memberReg.model;



import jakarta.persistence.*;
//import org.hibernate.tuple.GeneratedValueGeneration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "getRegisteredMembers",
                                    procedureName = "get_registered_members",
        resultClasses = Member.class)
})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String dbDevId;
    private String dbDevName;
    private String dbDevGender;
    private String dbDevAge;
}