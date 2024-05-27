package com.voice.yatraRegistration.memberReg.restController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.yatraRegistration.memberReg.model.Status;

@RestController
@RequestMapping("/v1/memReg")
@CrossOrigin("*")
public class RegisteredMemController {

    @Autowired
    private RegisterMemDao regMemDao;


    @Autowired
    MemberDao memberDao;

    @Autowired
    private UserAuthService userAuthService;

    @Value("${reg.mem.before.created.dateTime}")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime regMemBeforeCreateDateTime;

    @PostMapping("/saveInput")
    public ResponseEntity<RegisteredMember> insertDevoteeInfo(@RequestBody RegisteredMember input) {

        RegisteredMember r=regMemDao.save(input);

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
        List<RegisteredMember> allRegMem = regMemDao.findAll();
        for (RegisteredMember one : allRegMem) {
            if (one.getPaymentStatus().equalsIgnoreCase("success") ||
                    one.getPaymentStatus().equalsIgnoreCase(Status.APPROVED.name())) {
                List<Member> memList = one.getMemberIdList();
                for (Member mem : memList) {
                    result.add(mem.getDbDevId());
                }
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

}