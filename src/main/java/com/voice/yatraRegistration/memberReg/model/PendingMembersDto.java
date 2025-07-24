package com.voice.yatraRegistration.memberReg.model;

import lombok.Data;

@Data
public class PendingMembersDto {
    private Member member;
    private String payeeName;
    private String customerEmail;
    private String phoneNo;

    public PendingMembersDto(Member member, String payeeName, String customerEmail, String phoneNo) {
        this.member = member;
        this.payeeName = payeeName;
        this.customerEmail = customerEmail;
        this.phoneNo = phoneNo;
    }
}
