package com.voice.yatraRegistration.memberReg.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class YatraCreateOrderRequest {

	private String key;
	private String clientTransactionId;
	private String amount;
	private String info;
	private String customerName;
	private String customerEmail;
	private String customerMobile;
	private String callbackUrl;
	private String userDefined1;
	private String userDefined2;
	private String userDefined3;
}
