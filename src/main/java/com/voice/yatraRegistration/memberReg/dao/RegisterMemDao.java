package com.voice.yatraRegistration.memberReg.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterMemDao extends JpaRepository<RegisteredMember,String> {
    public RegisteredMember findByCustomerTxnId(String customerTxnId);
    public List<RegisteredMember> findAllByUserEmail(String email);
    public List<RegisteredMember> findAllByUpiTxnId(String upiTxnId);
    public List<RegisteredMember> findAllByPaymentStatus(String paymentStatus);
    public List<RegisteredMember> findAllByPaymentStatusNot(String paymentStatus);
    public List<RegisteredMember> findAllByCreatedDateTimeBefore(LocalDateTime createdDateTime);
}