package com.voice.yatraRegistration.memberReg.restController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.yatraRegistration.memberReg.model.ManualPaymentRequest;
import com.voice.yatraRegistration.memberReg.utils.common.YatraPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.voice.yatraRegistration.memberReg.dao.ManualPaymentRequestDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

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
    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/saveInput")
    public ManualPaymentRequest insertManualPaymentRequest(@RequestBody ManualPaymentRequest input) {
        return manualPaymentRequestDao.save(input);
    }

    @PostMapping("/fetchAllReq")
    public List<ManualPaymentRequest> fetchAll() {
        return manualPaymentRequestDao.findAll();
    }

    @PostMapping("/memRegAmt")
    public ResponseEntity<String> calculateYatraMemRegPaymentAmt(@RequestBody Map<String,Object> input, Authentication auth){
        Optional<UserAuth> user=userAuthService.getUserAuthFromAuthentication(auth);
        String userEmail = user.get().getUserEmail();
        String amount="";
        try {
            List<Map<String, Object>> devoteeList = (List<Map<String, Object>>) input.get("devoteeList");
            amount = String.valueOf(yatraPayment.calculateAmount(userEmail, devoteeList));
        }
        catch(Exception e){
            ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(amount);
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