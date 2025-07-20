package com.voice.payments.service;

import com.voice.payments.response.PaymentTokenResponse;
import com.voice.payments.utility.Aipay;
import com.voice.payments.utility.AtomSignature;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.service.RoomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${payment_domain_url}")
    private String paymentUrl;

    @Value("${merch_id}")
    private String merchId;

    @Value("${transaction_password}")
    private String transactionPassword;

    @Value("${product_id}")
    private String product;

    @Value("${request_encoding_key}")
    private String requestEncodingKey;

    @Value("${response_encoding_key}")
    private String responseEncodingKey;

    @Value("${return_url}")
    private String returnUrl;

    @Value("${resp_hash_key}")
    private String hashKey;

    @Autowired
    private RoomBookingService roomBookingService;

    public ResponseEntity<PaymentTokenResponse> initiatePayment(RoomBooking req) throws Exception {

        return Aipay.initTransactionForAipay(merchId,req.getAmount(),"GVS" + System.currentTimeMillis() + (int)(Math.random() * 10000),returnUrl,
                "",transactionPassword,req.getCustomerPhoneNo(),req.getCustomerEmail(),req.getCustomerName().split(" ")[0],"","",
                product,"INR",requestEncodingKey,responseEncodingKey,req.getId().toString(),"","","","",paymentUrl);


    }

    public void handlePaymentGatewayResponse(String encData,String merchId) throws Exception {

        String response=Aipay.response(encData, merchId, responseEncodingKey);
        System.out.println(response);

        String atomTxnId = Aipay.getValueFromJson(response,"atomTxnId").replaceAll("\"","");

        String bookingId = Aipay.getValueFromJson(response,"udf1");

        String status = Aipay.getValueFromJson(response,"message");

        String paymentMode = Aipay.getValueFromJson(response,"subChannel").replaceAll("[\\[\\]]","");

        String[] values={merchId,atomTxnId,Aipay.getValueFromJson(response,"merchTxnId"),Aipay.getValueFromJson(response,"totalAmount"),Aipay.getValueFromJson(response,"statusCode"),paymentMode,Aipay.getValueFromJson(response,"bankTxnId")};

        if(AtomSignature.generateSignature(hashKey,values).equals(Aipay.getValueFromJson(response,"signature").replaceAll("[\"]",""))){
             roomBookingService.saveResponseFromGateway(bookingId, atomTxnId, paymentMode, status);
        }
        else{
             roomBookingService.saveResponseFromGateway(bookingId,atomTxnId,paymentMode,"SUSPICIOUS");
        }

    }
}