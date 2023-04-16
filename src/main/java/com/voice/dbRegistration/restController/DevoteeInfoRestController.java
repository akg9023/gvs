package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.service.DatabaseService;
import com.voice.dbRegistration.utils.security.CustomSecurity;

@RestController
@RequestMapping("/v1/hlzGlobalReg")
@CrossOrigin(origins = "*")
public class DevoteeInfoRestController {

    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    DatabaseService databaseService;

    @PostMapping("/saveInput")
    public DevoteeInfo insertDevoteeInfo(@RequestBody DevoteeInfo input) {

        // dob shouldc be in "2020-12-31" format
        if (input.getCdob().length()!=0)
            input.setAge(Helper.calculateAge(input.getCdob()));
        // DevoteeInfo encrypted = encryptData(input);
        return databaseService.saveInputAndSendMessage(input);
        // return devoteeInfoDao.save(input);
    }

    private DevoteeInfo encryptData(DevoteeInfo input) {
        input.setPrimaryPhone(CustomSecurity.encrypt(input.getPrimaryPhone()));
        return input;
    }

    @PostMapping("/doesUserExist")
    public List<DevoteeInfo> doesExist(@RequestBody Map<String, String> input) {
        String email = input.get("email");
        return devoteeInfoDao.findAllByEmail(email);
    }

    @PostMapping("/fetchAllDepById/{userId}")
    public List<DevoteeInfo> fetchAllDepByConnectedId(@PathVariable("userId") String userId) {
        List<DevoteeInfo> dep = devoteeInfoDao.findAllByConnectedTo(userId);
        System.out.println(dep.size());
        return dep;
    }

    // localhost:8080/v1/hlzGlobalReg/fetchAllDev
    // localhost:8080/v1/hlzGlobalReg/saveInput

    @PostMapping("/fetchAllDevWithDecryption")
    public List<DevoteeInfo> fetchAllDevWithDec() {
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

    @PostMapping("/fetchAllDev")
    public List<DevoteeInfo> fetchAllDev() {
        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
        return allDev;
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
