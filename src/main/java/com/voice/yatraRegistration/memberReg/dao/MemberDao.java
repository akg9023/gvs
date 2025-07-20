package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import com.voice.yatraRegistration.memberReg.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberDao extends JpaRepository<Member,Long>,MemberDaoCustom {

    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
    public List<Member> findDuplicates();

    public Member findOneByDbDevId(String dbDevId);


}