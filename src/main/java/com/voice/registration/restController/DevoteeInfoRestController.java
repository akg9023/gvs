package com.voice.registration.restController;

import com.voice.registration.dao.DevoteeInfoDao;
import com.voice.registration.model.DevoteeInfoRequest;
import com.voice.registration.model.DevoteeInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin("*")
public class DevoteeInfoRestController {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @PostMapping("/saveInput")
    public DevoteeInfoResponse insertDevoteeInfo(@RequestBody DevoteeInfoResponse input){
        // System.out.println("Request");
        // System.out.println(input);

        // DevoteeInfoResponse devInfo = new DevoteeInfoResponse();

        // //set devotee id
        // devInfo.setId(Long.parseLong(getRandomId()));

        // //set password if user is parent
        // if(input.isParent())
        //     devInfo.setParentLoginPassword(getPassword(input.getDob()));

        // //convert dob
        // // input DOB Stringformat = <year>-<month>-<day> e.g "2020-09-08"
        // String dobInputString = input.getDob();
        // String dobYear = dobInputString.substring(0,4);
        // String dobMonth= dobInputString.substring(5,7);
        // String dobDay = dobInputString.substring(8,10);
        // devInfo.setDobDay(dobDay);
        // devInfo.setDobMonth(dobMonth);
        // devInfo.setDobYear(dobYear);
        return devoteeInfoDao.save(input);
        // System.out.println(deinpvoteeInfoDao);

        // return ;
    }

    private String getPassword(String dobString){
        String prefix = "HLZ";
        return prefix+dobString;
    }

    private String getRandomId(){
        // HLZ<8 digit unique num>
        String prefix = "HLZ";
        int x = (int)(System.currentTimeMillis()%Math.pow(10,8));
        String xString = String.valueOf(x);
        return prefix+xString;
    }

    @PostMapping("/uploadProfilePic")
    private String uploadImageGoogleDrive(){
        return "url";
    }
}
