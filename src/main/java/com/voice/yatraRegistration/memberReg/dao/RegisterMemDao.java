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



    @Query(value = "SELECT di.devotee_id, " +
            "CONCAT(di.fname, ' ', di.mname, ' ', di.lname) AS name, " +
            "di.date_of_birth, di.gender, " +
            "IF(di.whatsapp_phone <> '', di.whatsapp_phone, di.primary_phone) AS contact, " +
            "di.facilitator, di.chanting_rounds, " +
            "CONCAT(cdi.fname, ' ', cdi.mname, ' ', cdi.lname) AS registered_by, " +
            "IF(cdi.whatsapp_phone <> '', cdi.whatsapp_phone, cdi.primary_phone) AS contact_registered_by, " +
            "IFNULL(cdi.email, di.email) AS registered_by_email, " +
            "mi.paid_by_email AS paid_by_email, " +
            "CONCAT(ca.city, ', ', ca.state) AS current_city " +
            "FROM devotee_info di " +
            "JOIN address ca ON di.current_address_address_id = ca.address_id " +
            "LEFT JOIN devotee_info cdi ON di.connected_to = cdi.devotee_id " +
            "JOIN (SELECT DISTINCT m.db_dev_id AS id, m.db_dev_name AS name, m.db_dev_age AS age, " +
            "m.db_dev_gender AS gender, yarm.user_email AS paid_by_email " +
            "FROM prod.member m " +
            "JOIN prod.yatra_aug_23_reg_mem_member_id_list yarmmil ON m.id = yarmmil.member_id_list_id " +
            "JOIN prod.yatra_aug_23_reg_mem yarm ON yarm.yatra_mem_id = yarmmil.registered_member_yatra_mem_id) mi " +
            "ON di.devotee_id = mi.id " +
            "WHERE di.devotee_id NOT LIKE 'TMP23%'", nativeQuery = true)
    List<Object[]> findDevoteeDetailsRegisteredInYatraForEmail();

    @Query(value = "SELECT di.devotee_id, " +
            "CONCAT(di.fname, ' ', di.mname, ' ', di.lname) AS name, " +
            "di.date_of_birth, di.gender, " +
            "IF(di.whatsapp_phone <> '', di.whatsapp_phone, di.primary_phone) AS contact, " +
            "di.facilitator, di.chanting_rounds, " +
            "CONCAT(cdi.fname, ' ', cdi.mname, ' ', cdi.lname) AS registered_by, " +
            "IF(cdi.whatsapp_phone <> '', cdi.whatsapp_phone, cdi.primary_phone) AS contact_registered_by, " +
            "IFNULL(cdi.email, di.email) AS registered_by_email, " +
            "CONCAT(ca.city, ', ', ca.state) AS current_city " +
            "FROM devotee_info di " +
            "JOIN address ca ON di.current_address_address_id = ca.address_id " +
            "LEFT JOIN devotee_info cdi ON di.connected_to = cdi.devotee_id " +
            "WHERE di.devotee_id NOT LIKE 'TMP23%'", nativeQuery = true)
    List<Object[]> findDevoteeDetailsDBRegistrationForEmail();

    @Query(value = "SELECT di.devotee_id, CONCAT(di.fname, ' ', di.mname, ' ', di.lname) AS Name, " +
            "di.date_of_birth, di.gender, " +
            "IF(di.whatsapp_phone <> '', di.whatsapp_phone, di.primary_phone) AS contact, " +
            "di.facilitator, di.chanting_rounds, " +
            "CONCAT(cdi.fname, ' ', cdi.mname, ' ', cdi.lname) AS Registered_by, " +
            "IF(cdi.whatsapp_phone <> '', cdi.whatsapp_phone, cdi.primary_phone) AS Contact_Registered_by, " +
            "IFNULL(cdi.email, di.email) AS registered_by_email, " +
            "mi.paid_by_email AS paid_by_email, " +
            "CONCAT(ca.city, ', ', ca.state) AS Current_City, " +
            "accom.customer_email AS accom_email, " +
            "accom.customer_name AS accom_name, " +
            "accom.customer_phone_no AS accom_phone " +
            "FROM devotee_info di " +
            "JOIN address ca ON di.current_address_address_id = ca.address_id " +
            "LEFT JOIN devotee_info cdi ON di.connected_to = cdi.devotee_id " +
            "LEFT JOIN (SELECT DISTINCT m.db_dev_id AS id, m.db_dev_name AS name, m.db_dev_age AS age, " +
            "                  m.db_dev_gender AS gender, yarm.user_email AS paid_by_email " +
            "           FROM prod.member m " +
            "           JOIN prod.yatra_aug_23_reg_mem_member_id_list yarmmil " +
            "           ON m.id = yarmmil.member_id_list_id " +
            "           JOIN prod.yatra_aug_23_reg_mem yarm " +
            "           ON yarm.yatra_mem_id = yarmmil.registered_member_yatra_mem_id " +
            "           ) mi ON di.devotee_id = mi.id " +
            "LEFT JOIN (SELECT m.db_dev_id AS id, rb.customer_email, rb.customer_phone_no, rb.customer_name " +
            "           FROM prod.member m " +
            "           JOIN prod.room_set_member rsm " +
            "           ON rsm.member_id = m.id " +
            "           JOIN prod.room_booking_room_set rbrs " +
            "           ON rbrs.room_set_id = rsm.room_set_id " +
            "           JOIN prod.room_booking rb " +
            "           ON rbrs.room_booking_id = rb.id " +
            "           WHERE rb.payment_status NOT IN ('TIMEOUT','INITIATED') " +
            "           ) accom ON accom.id = di.devotee_id " +
            "WHERE di.devotee_id LIKE 'TMP23%'", nativeQuery = true)
    List<Object[]> findDevoteeDetailsTMPIdForEmail();

}