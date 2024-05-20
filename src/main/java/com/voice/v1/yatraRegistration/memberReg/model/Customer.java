package com.voice.v1.yatraRegistration.memberReg.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {

	private String id;
	private String customer_vpa;
	private String amount;
	private String client_txn_id;
	private String customer_name;
	private String customer_email;
	private String customer_mobile;
	private String p_info;
	private String upi_txn_id;
	private String status;
	private String remark;
	private String udf1;
	private String udf2;
	private String udf3;
	private String redirect_url;
	private String txnAt;
	private String createdAt;
	private Merchant merchantDetails;

}