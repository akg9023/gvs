package com.voice.v1.yatraRegistration.memberReg.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Response {

	private String status;
	private String msg;
	private Customer data;

}