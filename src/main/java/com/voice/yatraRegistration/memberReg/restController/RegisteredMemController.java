package com.voice.yatraRegistration.memberReg.restController;

import java.time.LocalDateTime;
import java.util.*;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.GetIDFnameGender;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.yatraRegistration.memberReg.model.Status;

@RestController
@RequestMapping("/v1/memReg")
@CrossOrigin("*")
public class RegisteredMemController {
    Logger logger = LoggerFactory.getLogger(RegisteredMemController.class);


    @Autowired
    private RegisterMemDao regMemDao;


    @Autowired
    MemberDao memberDao;

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Value("${reg.mem.before.created.dateTime}")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime regMemBeforeCreateDateTime;

    @PostMapping("/saveInput")
    public ResponseEntity<RegisteredMember> insertDevoteeInfo(@RequestBody RegisteredMember input,Authentication authentication) {

        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);

        input.setUserEmail(user.get().getUserEmail());
        input.setCustomerEmail(user.get().getUserEmail());

        List<Member> memList=new ArrayList<>();


        for(Member m: input.getMemberIdList()){

            Member mem= memberDao.findOneByDbDevId(m.getDbDevId());
            memList.add((mem == null)? m:mem );

        }
        input.setMemberIdList(memList);

        RegisteredMember r=null;
        try {
             r=regMemDao.save(input);
        }
        catch(Exception e){
            logger.error("save Registered Member Payment Failed",input,e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(r);
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<RegisteredMember>> fetchAll() {
        return ResponseEntity.ok(regMemDao.findAll());
    }

    @GetMapping("/fetchAllByEmail")
    public ResponseEntity<List<RegisteredMember>> fetchByEmail(Authentication authentication) {
         Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(userAuth -> ResponseEntity.ok(regMemDao.findAllByUserEmail(userAuth.getUserEmail()))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/fetchByClientTxnId/{clientTxnId}")
    public RegisteredMember fetchByClientTxnId(@PathVariable String clientTxnId) {
        return regMemDao.findByCustomerTxnId(clientTxnId);
    }

    @GetMapping("/fetchRegMemId")
    public ResponseEntity<List<String>> getAllRegMemDBId() {
        List<String> result = new ArrayList();
        //List<RegisteredMember> allRegMem = regMemDao.findAll();findDistinctByPaymentStatus
        List<RegisteredMember> allRegMem = regMemDao.findDistinctByPaymentStatusOrPaymentStatus(Status.SUCCESS.name().toLowerCase(),Status.PENDING.name().toLowerCase());
        if(allRegMem!=null)
         if(!allRegMem.isEmpty())
          for (RegisteredMember one : allRegMem) {
                List<Member> memList = one.getMemberIdList();
                for (Member mem : memList) {
                    result.add(mem.getDbDevId());

                }
         }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deleteMemReg")
    public void delete(@RequestBody RegisteredMember registeredMember) {
        regMemDao.delete(registeredMember);
    }

    @GetMapping("/successMembers")
    public ResponseEntity<List<Member>> getByCreatedDateTime(){
        return  ResponseEntity.ok(memberDao.getAllSuccessMemBeforeDate());
    }
    @GetMapping("/fetchDevWithLimitedData/{userId}")
    public ResponseEntity<GetIDFnameGender> fetchADevWithLimitedData(@PathVariable("userId") String devId) {
        GetIDFnameGender dev;

        try {
             dev = devoteeInfoDao.findDev(devId);
        }
        catch (Exception e){

            return ResponseEntity.internalServerError().build();
        }


        return ResponseEntity.ok(dev);
    }
    @GetMapping("/fetchDependentsWithLimitedData")
    public ResponseEntity<List<GetIDFnameGender>> fetchLoggedInUsersDependents(Authentication authentication) {

        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(authentication);

        List<GetIDFnameGender> dev;

        try {
            dev = devoteeInfoDao.findDevHavingConnectedTo(user.get().getUserId());
        }
        catch (Exception e){

            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(dev);
    }

}