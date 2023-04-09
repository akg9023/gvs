package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.model.Enums;
import com.voice.dbRegistration.utils.security.CustomSecurity;

@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin("*")
public class DevoteeInfoRestController {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @PostMapping("/saveInput")
    public DevoteeInfo insertDevoteeInfo(@RequestBody DevoteeInfo input) {

        // dob should be in "2020-12-31" format
        if (input.getDob().length()!=0)
            input.setAge(Helper.calculateAge(input.getDob()));
        DevoteeInfo encrypted = encryptData(input);
        return devoteeInfoDao.save(encrypted);
        // return devoteeInfoDao.save(input);
    }

    private DevoteeInfo encryptData(DevoteeInfo input) {
        input.setPrimaryPhone(CustomSecurity.encrypt(input.getPrimaryPhone()));
        return input;
    }

    @PostMapping("/doesUserExist")
    public DevoteeInfo doesExist(@RequestBody Map<String, String> input) {
        String email = input.get("email");
        return devoteeInfoDao.findOneByEmail(email);
    }

    @PostMapping("/fetchAllDepById/{userId}")
    public List<DevoteeInfo> fetchAllDepByConnectedId(@PathVariable("userId") String userId) {
        List<DevoteeInfo> dep = devoteeInfoDao.findAllByConnectedTo(userId);
        System.out.println(dep.size());
        return dep;
    }

    // localhost:8080/v1/hlzGlobalReg/fetchAllDev
    // localhost:8080/v1/hlzGlobalReg/saveInput

    @PostMapping("/fetchAllDev")
    public List<DevoteeInfo> fetchAllDev() {
        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
        try {
            allDev = (List<DevoteeInfo>) allDev.stream().map(oneDev -> {
                oneDev.setPrimaryPhone(CustomSecurity.decrypt(oneDev.getPrimaryPhone()));
                return oneDev;
            }).collect(Collectors.toList());
            return allDev;
        } catch (Exception e) {
            System.out.println("Failed to decrpyt the data");
            return new ArrayList();

        }
    }

    @PostMapping("/fetchSpecefic/{userId}")
    public DevoteeInfo fetchSpecefic(@PathVariable("userId") String userId) {
        return devoteeInfoDao.findOneById(userId);
    }

    @PostMapping("/uploadProfilePic")
    private String uploadImageGoogleDrive() {
        return "url";
    }

}
