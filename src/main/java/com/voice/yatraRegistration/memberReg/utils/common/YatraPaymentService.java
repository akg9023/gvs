package com.voice.yatraRegistration.memberReg.utils.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class YatraPaymentService {

    @Value("${register.member.amount}")
    private int perHeadRegAmount;

    @Value("${register.member.exempted.age}")
    private int exemptedAge;

    @Value("${register.member.teen.age}")
    private int teenAge;

    @Value("${register.member.teen.amount}")
    private int teensAmount;

    @Value("${register.member.volunteer.email}")
    private String volunteerEmail;

    @Value("${register.member.volunteer.amount}")
    private int volunteerPerHeadAmount;
    
    public int calculateAmount(String userEmail,List<Map<String,Object>> devoteeList ){


        int regularAmount = perHeadRegAmount;
        int teenAmountCopy = teensAmount;

         // special consession for volunteer email id
         if(userEmail.equals(volunteerEmail)){
            regularAmount = volunteerPerHeadAmount;
            teenAmountCopy = volunteerPerHeadAmount;
        }

        // no charge for children 
        int countChild = 0;
        int teens = 0;
        for (Map<String,Object> one : devoteeList) {
            String temp = (String) one.get("age");
            int devAge = Integer.parseInt(temp);
            if(devAge<=exemptedAge)
                countChild++;
            else if(devAge<=teenAge){
                teens++;
            }
        }

    
        int teenAmt = teens*teenAmountCopy;
        int adultAmount = (devoteeList.size()-countChild-teens)*regularAmount;
        int amount = teenAmt+adultAmount;


        return amount;
    }
}
