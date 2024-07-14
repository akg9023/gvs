package com.voice.yatraRegistration.memberReg.restController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.voice.yatraRegistration.memberReg.model.ManualPaymentRequest;
import com.voice.yatraRegistration.memberReg.utils.readExcelAndUpdate.ExtractBankTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//import com.voice.dbRegistration.service.SendSmsService;
import com.voice.yatraRegistration.memberReg.dao.ManualPaymentRequestDao;
import com.voice.yatraRegistration.memberReg.dao.RegisterMemDao;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import com.voice.yatraRegistration.memberReg.model.Transaction;

//@RestController
//@RequestMapping("/v1/update/rpt/")
@CrossOrigin("*")
public class ExcelUpdateDB {

    @Autowired
    RegisterMemDao regDao;

    @Autowired
    ManualPaymentRequestDao manualPaymentRequestDao;

//    @Autowired
//    SendSmsService sendSmsService;

    @PostMapping("/validateTxn")
    public List<RegisteredMember> insertManualPaymentRequest(@RequestBody MultipartFile file) throws IOException {
        ExtractBankTransactions extractBankTransactions = new ExtractBankTransactions(file.getInputStream());
        // Transaction t1 = extractBankTransactions.extractTransaction("236435933480");
        // Transaction t2 = extractBankTransactions.extractTransaction("313105960142");
        // System.out.println(t1);
        // System.out.println(t2);

        String declineDueToAmount = "DECLINE - amount not matched";
        String declineDueToTxnId = "DECLINE - transactionId incorrect";
        String currentStatus = "";
        List<RegisteredMember> pendingRegMemList = regDao.findAllByPaymentStatus("pending");

        for (int i = 0; i < pendingRegMemList.size(); i++) {
            RegisteredMember temp = pendingRegMemList.get(i);
            String txnId = temp.getUpiTxnId();
            LocalDate txnDate = temp.getCreatedDateTime().toLocalDate();
            Transaction txnDetails = extractBankTransactions.extractTransaction(txnId);
            if (txnDetails != null) {
                int excelAmount = txnDetails.getTransactionAmount().intValue();
                int dbAmount = Integer.parseInt(temp.getAmount());

                // validating
                if (excelAmount == dbAmount) {
                    currentStatus = "success";
                    temp.setCustomerVPA(txnDetails.getUpiId());
                    temp.setPaymentStatus("success");
                } else {
                    currentStatus = declineDueToAmount;
                    temp.setCustomerVPA(txnDetails.getUpiId());
                    temp.setPaymentStatus(declineDueToAmount);
                }
            } else if (!txnDate.isAfter(extractBankTransactions.getLastDate())) {
                currentStatus = declineDueToTxnId;
                temp.setPaymentStatus(declineDueToTxnId);
            } else continue;
            regDao.save(temp);

            // send text message
            ManualPaymentRequest cust = manualPaymentRequestDao.findByCustomerUTR(txnId);
            String phoneNo = cust.getCustomerPhoneNo();
            String message = "HareKrsna" + "\n"
                    + "TxnId: " + txnId + "\n"
                    + "Status: " + currentStatus;
           // sendSmsService.sendSms(message, phoneNo);
        }

        return pendingRegMemList;

    }

}