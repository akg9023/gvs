package com.voice.yatraRegistration.memberReg.dao;

import com.voice.common.model.YatraRegEmail;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RegisterMemDao extends JpaRepository<RegisteredMember, String> {
    public RegisteredMember findByCustomerTxnId(String customerTxnId);

    public List<RegisteredMember> findAllByUserEmail(String email);

    public List<RegisteredMember> findAllByUpiTxnId(String upiTxnId);

    public List<RegisteredMember> findAllByPaymentStatus(String paymentStatus);

    public List<RegisteredMember> findAllByPaymentStatusNot(String paymentStatus);

    public List<RegisteredMember> findAllByCreatedDateTimeBefore(LocalDateTime createdDateTime);

    @Query(value = "SELECT COALESCE(user.yatra_mem_id,''), " +
            "COALESCE(user.user_id,''), " +
            "COALESCE(user.user_email,''), " +
            "COALESCE(user.user_fname,''), " +
            "COALESCE(user.user_lname,''), " +
            "COALESCE(di2.devotee_id,'') AS member_id, " +
            "COALESCE(di2.fname,''), " +
            "COALESCE(di2.lname,''), " +
            "COALESCE(di2.date_of_birth,''), " +
            "COALESCE(di2.primary_phone,''), " +
            "COALESCE(di2.gender,''), " +
            "COALESCE (city, '')" +
            "FROM (SELECT ya.yatra_mem_id, " +
            "             di.devotee_id AS user_id, " +
            "             ya.user_email, " +
            "             di.fname AS user_fname, " +
            "             di.lname AS user_lname " +
            "      FROM yatra_aug_23_reg_mem ya " +
            "      JOIN devotee_info di ON ya.user_email = di.email " +
            "      WHERE di.connected_to = 'guru') AS user " +
            "JOIN yatra_aug_23_reg_mem_member_id_list ym ON user.yatra_mem_id = ym.registered_member_yatra_mem_id " +
            "JOIN `member` m ON m.id = ym.member_id_list_id " +
            "JOIN devotee_info di2 ON di2.devotee_id = m.db_dev_id " +
            "JOIN address a ON a.address_id = di2.current_address_address_id",
            nativeQuery = true)
    public List<Object[]> getAllUserWithMembersRegistredYatra();

}