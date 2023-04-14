package com.voice.dbRegistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voice.common.utils.SendSmsService;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;

@Service
public class DatabaseService {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;
    
    public DevoteeInfo saveInputAndSendMessage(DevoteeInfo devoteeInfo){
        DevoteeInfo devotee = devoteeInfoDao.save(devoteeInfo);
        String message = "Hare Krishna! "+ devotee.getFname()+" \n"+
            "Your registration id is : "+ devotee.getId()+"\n"+
            "-in service \n"+
            "Haldia VOICE";
          
        SendSmsService.sendSms(message, devotee.getPrimaryPhone());
        return devotee;
    }
}
