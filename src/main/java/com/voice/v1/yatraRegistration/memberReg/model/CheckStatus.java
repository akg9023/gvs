package com.voice.v1.yatraRegistration.memberReg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CheckStatus {

	private String client_txn_id;
	private String txn_date;
	private String key;

}