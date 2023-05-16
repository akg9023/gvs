package com.voice.yatraRegistration.memberReg.restController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.yatraRegistration.memberReg.dao.ManualPaymentRequestDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.ManualPaymentRequest;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.yatraRegistration.memberReg.utils.common.YatraPaymentService;

@RestController
@RequestMapping("/v1/manualPayment")
@CrossOrigin("*")
public class ManualPaymentRequestController {

    @Autowired
    YatraPaymentService yatraPayment;
    
    @Autowired
    ManualPaymentRequestDao manualPaymentRequestDao;

    @Autowired
    private RegisterMemDao registerMemDao;

    @PostMapping("/saveInput")
    public ManualPaymentRequest insertManualPaymentRequest(@RequestBody ManualPaymentRequest input) {
        return manualPaymentRequestDao.save(input);
    }

    @PostMapping("/fetchAllReq")
    public List<ManualPaymentRequest> fetchAll() {
        return manualPaymentRequestDao.findAll();
    }

    @PostMapping("/memRegAmt")
    public String calculateYatraMemRegPaymentAmt(@RequestBody Map<String,Object> input){
        String userEmail = (String)input.get("userEmail");
        List<Map<String,Object>> devoteeList = (List<Map<String, Object>>) input.get("devoteeList");
        String amount = String.valueOf(yatraPayment.calculateAmount(userEmail, devoteeList));
        return amount;
    }

    @PostMapping("/updateRegMem")
    public List<RegisteredMember> updateRegMem(){
        List<ManualPaymentRequest> allNonPendingReq = manualPaymentRequestDao.findAllNonPendingEntries();
        List<RegisteredMember> updatedList = new ArrayList<>();
        for(ManualPaymentRequest one:allNonPendingReq){
            RegisteredMember regMem = updateStatus(one.getCustomerUTR(), one.getStatus().name());
            updatedList.add(regMem);
        }
        return updatedList;
    }

    public RegisteredMember updateStatus(String txnId,String status){
        List<RegisteredMember> listRegMemWithTxnId = registerMemDao.findAllByUpiTxnId(txnId);
        RegisteredMember regMemWithTxnId = listRegMemWithTxnId.get(0);
        regMemWithTxnId.setPaymentStatus(status);
        return registerMemDao.save(regMemWithTxnId);
    }   

}
