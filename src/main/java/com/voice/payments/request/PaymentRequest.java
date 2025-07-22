package com.voice.payments.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String amount;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNo;
}

