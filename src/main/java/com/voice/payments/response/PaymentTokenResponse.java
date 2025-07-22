package com.voice.payments.response;

import lombok.Data;

@Data
public class PaymentTokenResponse {
    private String atomTokenId;
    private String merchId;
    private String custEmail;
    private String custMobile;
    private String returnUrl;

}
