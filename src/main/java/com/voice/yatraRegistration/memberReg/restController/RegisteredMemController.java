package com.voice.yatraRegistration.memberReg.restController;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public RegisteredMember insertDevoteeInfo(@RequestBody RegisteredMember input){
        
        return regMemDao.save(input);
    }

    @PostMapping("/fetchAll")
    public List<RegisteredMember> fetchAll(){
        return regMemDao.findAll();
    }

    @PostMapping("/fetchByClientTxnId/{clientTxnId}")
    public RegisteredMember fetchByClientTxnId(@PathVariable String clientTxnId){

        return regMemDao.findByCustomerTxnId(clientTxnId);
    }
   

}
