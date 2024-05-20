package com.voice.v1.yatraRegistration.memberReg.dao;

import java.util.List;

import com.voice.v1.yatraRegistration.memberReg.model.Member;

public interface MemberDaoCustom  {
    List<Member> getAllSuccessMemBeforeDate();

}