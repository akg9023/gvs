package com.voice.v1.dbRegistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voice.v1.dbRegistration.dao.DevoteeInfoDao;
import com.voice.v1.dbRegistration.model.DevoteeInfo;

@Service
public class DatabaseService {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

//    @Autowired
//    SendSmsService sendSmsService;

    public DevoteeInfo saveInputAndSendMessage(DevoteeInfo devoteeInfo){
        DevoteeInfo devotee = devoteeInfoDao.save(devoteeInfo);
        String message = "Hare Krishna! "+ devotee.getFname()+" \n"+
            "Your registration id is : "+ devotee.getId()+"\n"+
            "-in service \n"+
            "Haldia VOICE";
        // SendSmsService sms = new SendSmsService();
        //sendSmsService.sendSms(message, devotee.getPrimaryPhone());
        return devotee;
    }
}