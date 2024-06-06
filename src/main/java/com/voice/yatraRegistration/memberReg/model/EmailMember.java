package com.voice.yatraRegistration.memberReg.model;

import com.voice.common.model.YatraRegEmail;
import com.voice.dbRegistration.model.DevoteeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailMember {

    private String id;
    private String email;
    private String userFname;
    private String userLname;
    private Map<String, YatraRegEmail> dbRegistrationMemberMap;
    private Map<String, YatraRegEmail> yatraRegistrationMemberMap;

}