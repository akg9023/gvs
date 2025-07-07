package com.voice.payments.controllers;


import com.voice.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/transaction-response")
    public void postResponse(String encData, String merchId) throws Exception {
        paymentService.handlePaymentGatewayResponse(encData,merchId);
    }
}