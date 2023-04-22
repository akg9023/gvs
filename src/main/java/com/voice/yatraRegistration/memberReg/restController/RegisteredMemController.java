package com.voice.yatraRegistration.memberReg.restController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

@RestController
@RequestMapping("/v1/memReg")
@CrossOrigin("*")
public class RegisteredMemController {

    @Autowired
    private RegisterMemDao regMemDao;

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
            if (one.getPaymentStatus().equals("success")){
                List<Member> memList = one.getMemberIdList();
                for (Member mem : memList) {
                    result.add(mem.getDbDevId());
                }
            }
        }
        return result;
    }

}
