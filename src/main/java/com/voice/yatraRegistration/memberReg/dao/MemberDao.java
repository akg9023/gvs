package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.PendingMembersDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberDao extends JpaRepository<Member,Long>,MemberDaoCustom {

    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
    public List<Member> findDuplicates();

    public Member findOneByDbDevId(String dbDevId);

    @Query("""
    SELECT m,rm.payeeName,rm.customerEmail,rm.phoneNo FROM RegisteredMember rm 
    JOIN rm.memberIdList m 
    WHERE rm.paymentStatus = 'success'
    AND (
        m NOT IN (
            SELECT mem FROM RoomBooking rb 
            JOIN rb.roomSet rs 
            JOIN rs.member mem
        )
        OR NOT EXISTS (
            SELECT 1 FROM RoomBooking rb2 
            JOIN rb2.roomSet rs2 
            JOIN rs2.member mem2 
            WHERE mem2 = m AND rb2.paymentStatus = 'SUCCESS'
        )
    )
    """)
    List<PendingMembersDto> findRegisteredMembersWithNoRoomOrNonSuccessBooking();


}