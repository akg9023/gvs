package com.voice.v1.yatraRegistration.memberReg.restController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.v1.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.v1.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.v1.yatraRegistration.memberReg.model.Member;
import com.voice.v1.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.v1.yatraRegistration.memberReg.model.Status;

@RestController
@RequestMapping("/v1/memReg")
@CrossOrigin("*")
public class RegisteredMemController {

    @Autowired
    private RegisterMemDao regMemDao;


    @Autowired
    MemberDao memberDao;

    @Value("${reg.mem.before.created.dateTime}")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime regMemBeforeCreateDateTime;

    @PostMapping("/saveInput")
    public RegisteredMember insertDevoteeInfo(@RequestBody RegisteredMember input) {

        return regMemDao.save(input);
    }

    @PostMapping("/fetchAll")
    public List<RegisteredMember> fetchAll() {
        return regMemDao.findAll();
    }

    @PostMapping("/fetchAllByEmail")
    public List<RegisteredMember> fetchByEmail(@RequestBody Map<String, String> input) {
        String email = input.get("email");
        return regMemDao.findAllByUserEmail(email);
    }

    @PostMapping("/fetchByClientTxnId/{clientTxnId}")
    public RegisteredMember fetchByClientTxnId(@PathVariable String clientTxnId) {
        return regMemDao.findByCustomerTxnId(clientTxnId);
    }

    @PostMapping("/fetchRegMemId")
    public List<String> getAllRegMemDBId() {
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
        return result;
    }

    @DeleteMapping("/deleteMemReg")
    public void delete(@RequestBody RegisteredMember registeredMember) {
        regMemDao.delete(registeredMember);
    }

    @PostMapping("/successMembers")
    public List<Member> getByCreatedDateTime(){
        return  memberDao.getAllSuccessMemBeforeDate();
    }

}