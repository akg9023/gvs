package com.voice.registration.restController;

import com.voice.registration.dao.DevoteeInfoDao;
import com.voice.registration.model.DevoteeInfo;
import com.voice.registration.utils.security.CustomSecurity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin("*")
public class DevoteeInfoRestController {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @PostMapping("/saveInput")
    public DevoteeInfo insertDevoteeInfo(@RequestBody DevoteeInfo input){

        DevoteeInfo encrypted = encryptData(input);
        return devoteeInfoDao.save(encrypted);

        // return devoteeInfoDao.save(input);
    }

    private DevoteeInfo encryptData(DevoteeInfo input) {
        input.setPrimaryPhone(CustomSecurity.encrypt(input.getPrimaryPhone()));
        return input;
    }

    @PostMapping("/doesUserExist")
    public boolean doesExist(@RequestBody Map<String, String> input) {
        String email = input.get("email");
            return devoteeInfoDao.existsByEmail(email);
    }

    @PostMapping("/fetchAllDepByEmail")
    public List<DevoteeInfo> fetchDep(@RequestBody Map<String, String> input) {
        String email = input.get("email");
        List<DevoteeInfo> dep = devoteeInfoDao.findAllByConnectedEmail(email);
        return dep;
    }

    // localhost:8080/v1/hlzGlobalReg/fetchAllDev
    // localhost:8080/v1/hlzGlobalReg/saveInput


    @PostMapping("/fetchAllDev")
    public List<DevoteeInfo> fetchAllDev() {
        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
        try{
            allDev = (List<DevoteeInfo>) allDev.stream().map(oneDev -> {
                oneDev.setPrimaryPhone(CustomSecurity.decrypt(oneDev.getPrimaryPhone()));
                return oneDev;
            }).collect(Collectors.toList());
            return allDev;
        }
        catch(Exception e){
            System.out.println("Failed to decrpyt the data");
            return new ArrayList();
        }
    }


    @PostMapping("/uploadProfilePic")
    private String uploadImageGoogleDrive(){
        return "url";
    }


}
