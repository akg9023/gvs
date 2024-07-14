package com.voice.yatraRegistration.memberReg.model;

import com.voice.common.model.YatraRegEmail;
import com.voice.dbRegistration.model.DevoteeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailMember {
    //For Yatra Registration
    private String paidByEmail;
    //For DB Registration
    private String registeredByEmail;
    private Map<String, YatraRegEmail> dbRegistrationMemberMap;
    private Map<String, YatraRegEmail> yatraRegistrationMemberMap;

    //For TMP ID
    private String tmpIdSendEmailTo;
    private Map<String, YatraRegEmail> tmpIdRegistrationMemberMap;

}