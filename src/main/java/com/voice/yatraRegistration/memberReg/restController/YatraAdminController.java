package com.voice.yatraRegistration.memberReg.restController;


import com.voice.auth.model.Role;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.yatraRegistration.memberReg.model.EmailMember;
import com.voice.yatraRegistration.memberReg.service.YatraAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/yatra/admin")
public class YatraAdminController {
    Logger logger = LoggerFactory.getLogger(YatraAdminController.class);

    @Autowired
    private YatraAdminService yatraAdminService;
//    @GetMapping("/sendEmail")
//    public ResponseEntity<Map<String, EmailMember>> sendEmailWithMemberId(){
//
//            return ResponseEntity.ok(yatraAdminService.sendEmailWithMemberDetails());
//
//    }
//    @GetMapping("/sendTmpEmail")
//    public ResponseEntity<Map<String, EmailMember>> sendTMPIdEmailWithMemberId(){
//
//        return ResponseEntity.ok(yatraAdminService.sendEmailWithMemberDetailsForTMPId());
//
//    }

    /**
     *
     * @param startDate  and endDate (format yyyy-mm-dd)
     * @return list of all db registrations within this period
     */
    @GetMapping("/dbRegWithin")
    public ResponseEntity<List<DevoteeInfo>> getDevoteeInfoWithingDateRange( @RequestParam String startDate, @RequestParam String endDate){
        Optional<List<DevoteeInfo>> result = yatraAdminService.getDevoteeInfoWithinDateRange(startDate, endDate);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }
    @PostMapping("/role")
    public ResponseEntity<?> saveRole(@RequestBody Role role){
        Optional<Role> result = yatraAdminService.saveRole(role);
        if(result.isPresent()){
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.badRequest().body("Either Role is null or Role prefix is not present ROLE_");
    }
}