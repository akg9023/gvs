package com.voice.yatraRegistration.memberReg.restController;


import com.voice.auth.model.Role;
import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.PendingMembersDto;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
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
    RegisterMemDao registerMemDao;

    @Autowired
    RoomBookingDao bookingDao;

    @Autowired
    MemberDao memberDao;

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
    public ResponseEntity<List<Object>> getDevoteeInfoWithingDateRange( @RequestParam String startDate, @RequestParam String endDate){
        Optional<List<Object>> result = yatraAdminService.getDevoteeInfoWithinDateRange(startDate, endDate);
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

    @GetMapping("/fetchYatraRegisteredmembers")
    public ResponseEntity<?> fetchYatraRegisteredmembers(){
        Optional<List<RegisteredMember>> result = Optional.of(registerMemDao.findAll());
        if(result.isPresent()){
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.badRequest().body("Either Access is Denied or Session Expired");
    }

    @GetMapping("/fetchAllBookedMembers")
    public ResponseEntity<List<RoomBooking>> getAllBookedMembers(){
        return ResponseEntity.ok(bookingDao.findAll());
    }

    @GetMapping("/fetchAllPendingMembersForRoomBooking")
    public ResponseEntity<List<PendingMembersDto>> getAllPendingMembersForRoomBooking() {
      return  ResponseEntity.ok(memberDao.findRegisteredMembersWithNoRoomOrNonSuccessBooking());

    }

}