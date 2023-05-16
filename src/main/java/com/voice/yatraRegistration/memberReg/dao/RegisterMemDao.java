package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

public interface RegisterMemDao extends JpaRepository<RegisteredMember,String> {
    public RegisteredMember findByCustomerTxnId(String customerTxnId);
    public List<RegisteredMember> findAllByUserEmail(String email);
    public List<RegisteredMember> findAllByUpiTxnId(String upiTxnId);

}   
