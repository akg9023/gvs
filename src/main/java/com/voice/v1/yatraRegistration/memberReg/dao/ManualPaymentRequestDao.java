package com.voice.v1.yatraRegistration.memberReg.dao;

import java.util.List;

import com.voice.v1.yatraRegistration.memberReg.model.ManualPaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManualPaymentRequestDao extends JpaRepository<ManualPaymentRequest,Long> {

    @Query(value = "SELECT m FROM ManualPaymentRequest m where m.status <> 'PENDING'")
    public List<ManualPaymentRequest> findAllNonPendingEntries();
    public ManualPaymentRequest findByCustomerUTR(String customerUTR);


}