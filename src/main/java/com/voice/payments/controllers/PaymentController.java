package com.voice.payments.controllers;


import com.voice.payments.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Value("${yatra_redirect_url}")
    String yatraRedirectUrl;

    @PostMapping("/transaction-response")
    public ResponseEntity<Void> postResponse(HttpServletResponse response, String encData, String merchId) throws Exception {
        paymentService.handlePaymentGatewayResponse(encData,merchId);

        response.sendRedirect(yatraRedirectUrl);

        return null;

    }
}