package com.voice.yatraRegistration.memberReg.restController;


import com.voice.yatraRegistration.memberReg.model.EmailMember;
import com.voice.yatraRegistration.memberReg.service.YatraAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/yatra/admin")
public class YatraAdminController {
    Logger logger = LoggerFactory.getLogger(YatraAdminController.class);

    @Autowired
    private YatraAdminService yatraAdminService;
    @GetMapping("/sendEmail")
    public ResponseEntity<Map<String, EmailMember>> sendEmailWithMemberId(){

            return ResponseEntity.ok(yatraAdminService.sendEmailWithMemberDetails());

    }
}