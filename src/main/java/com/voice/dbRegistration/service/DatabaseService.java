package com.voice.dbRegistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;

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
    public String getMaskedPrimaryPhone(String phone){
        if(!phone.isEmpty()){
            String maskedStr = "XXXXXX";
            if(phone.length()==10){
                return maskedStr+phone.substring(6);
            }
        }
       return "NA";

    }
    public String getLastDevId(){
        return devoteeInfoDao.getLastDevId();
    }
}