package com.voice.dbRegistration.restController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.dbRegistration.model.GetIDFnameGender;
import com.voice.dbRegistration.service.DatabaseService;
import com.voice.dbRegistration.utils.security.CustomSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.DevoteeInfo;

@RestController
@RequestMapping("/v1/hlzGlobalReg")
//@CrossOrigin(origins = "*")
public class DevoteeInfoRestController {
    Logger logger = LoggerFactory.getLogger(DevoteeInfoRestController.class);
    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Autowired
    DatabaseService databaseService;

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/saveInput")
    public ResponseEntity<DevoteeInfo> insertDevoteeInfo(@RequestBody DevoteeInfo input,Authentication authentication) {
        Optional<UserAuth> user =  userAuthService.getUserAuthFromAuthentication(authentication);
        // dob shouldc be in "2020-12-31" format
        if (!input.getDateOfBirth().isEmpty())
            input.setAge(Helper.calculateAge(input.getDateOfBirth()));
        // DevoteeInfo encrypted = encryptData(input);
        if(input.getConnectedTo().isEmpty() && user.isPresent() && !user.get().getUserId().isEmpty()){
            input.setConnectedTo(user.get().getUserId());
        }
        DevoteeInfo devoteeInfo =  databaseService.saveInputAndSendMessage(input);

        if(user.isPresent()){
            UserAuth userAuth = user.get();
            if(userAuth.getUserEmail().equals(devoteeInfo.getEmail()) && devoteeInfo.getConnectedTo().equals("guru")){
                Optional<UserAuth> res = userAuthService.updateUserAuthWhenDevoteeInfoSaveSelf(devoteeInfo);

               if(res.isPresent()){
                   if(userAuthService.setUserAuthInSessionAuthentication(authentication,res.get())){
                       logger.info("Session updated after devoteeInfo insert {}", res.get());
                   }
                   logger.debug("UserAuth  updated after devoteeInfo insert {}", res.get());

               }
            }
        }
        return ResponseEntity.ok(devoteeInfo);
    }

    private DevoteeInfo encryptData(DevoteeInfo input) {
        input.setPrimaryPhone(CustomSecurity.encrypt(input.getPrimaryPhone()));
        return input;
    }

    @GetMapping("/doesUserExist")
    public ResponseEntity<DevoteeInfo> doesExist(Authentication authentication) {
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        if(user.isPresent()){

            DevoteeInfo devoteeInfo= devoteeInfoDao.findByEmailAndConnectedTo(user.get().getUserEmail(),"guru");
            return ResponseEntity.ok(devoteeInfo);

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/fetchAllDepById")
    public ResponseEntity<List<DevoteeInfo>> fetchAllDepByConnectedId(Authentication authentication) {
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(userAuth -> ResponseEntity.ok(devoteeInfoDao.findAllByConnectedTo(user.get().getUserId()))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // localhost:8080/v1/hlzGlobalReg/fetchAllDev
    // localhost:8080/v1/hlzGlobalReg/saveInput

//    @PostMapping("/fetchAllDevWithDecryption")
//    public List<DevoteeInfo> fetchAllDevWithDec() {
//        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
//        try {
//            allDev = (List<DevoteeInfo>) allDev.stream().map(oneDev -> {
//                oneDev.setPrimaryPhone(CustomSecurity.decrypt(oneDev.getPrimaryPhone()));
//                return oneDev;
//            }).collect(Collectors.toList());
//            return allDev;
//        } catch (Exception e) {
//            System.out.println("Failed to decrpyt the data");
//            return new ArrayList();
//
//        }
//    }
//
//    @PostMapping("/fetchAllDev")
//    public List<DevoteeInfo> fetchAllDev() {
//        List<DevoteeInfo> allDev = devoteeInfoDao.findAll();
//        return allDev;
//    }
//
//    @PostMapping("/fetchAllDevWithLimitedData")
//    public List<GetIDFnameGender> fetchAllDevWithLimitedData() {
//        List<GetIDFnameGender> allDev = devoteeInfoDao.findAllDev();
//        return allDev;
//    }
//
//    @PostMapping("/fetchSpecefic/{userId}")
//    public DevoteeInfo fetchSpecefic(@PathVariable("userId") String userId) {
//        return devoteeInfoDao.findOneById(userId);
//    }
//
//    @DeleteMapping("/deleteInfo")
//    private void deleteDevoteeInfo(@RequestBody DevoteeInfo devoteeInfo) {
//         devoteeInfoDao.delete(devoteeInfo);
//    }

}