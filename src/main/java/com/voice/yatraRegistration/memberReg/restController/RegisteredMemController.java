package com.voice.yatraRegistration.memberReg.restController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.model.GetIDFnameGender;
import com.voice.dbRegistration.restController.DevoteeInfoRestController;
import com.voice.yatraRegistration.memberReg.dao.Member24Dao;
import com.voice.yatraRegistration.memberReg.model.Member24;
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

import static org.springframework.http.ResponseEntity.badRequest;

@RestController
@RequestMapping("/v1/memReg")
@CrossOrigin("*")
public class RegisteredMemController {
    Logger logger = LoggerFactory.getLogger(RegisteredMemController.class);


    @Autowired
    private RegisterMemDao regMemDao;


    @Autowired
    Member24Dao member24Dao;

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    DevoteeInfoDao devoteeInfoDao;

    @Value("${reg.mem.before.created.dateTime}")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime regMemBeforeCreateDateTime;

    @PostMapping("/saveInput")
    public ResponseEntity<RegisteredMember> insertDevoteeInfo(@RequestBody RegisteredMember input) {

        List<Member24> memList=new ArrayList<>();


        for(Member24 m: input.getMemberIdList()){

            Member24 mem=member24Dao.findOneByDbDevId(m.getDbDevId());
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
                List<Member24> memList = one.getMemberIdList();
                for (Member24 mem : memList) {
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
    public ResponseEntity<List<Member24>> getByCreatedDateTime(){
        return  ResponseEntity.ok(member24Dao.getAllSuccessMemBeforeDate());
    }
    @GetMapping("/fetchDevWithLimitedData/{userId}")
    public ResponseEntity<GetIDFnameGender> fetchADevWithLimitedData(@PathVariable("userId") String devId) {
        GetIDFnameGender dev = devoteeInfoDao.findDev(devId);
        return ResponseEntity.ok(dev);
    }

}