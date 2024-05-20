package com.voice.v1.yatraRegistration.memberReg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voice.v1.yatraRegistration.memberReg.model.Member;

public interface MemberDao extends JpaRepository<Member,Long>,MemberDaoCustom {

    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
    public List<Member> findDuplicates();

    public List<Member> findByDbDevId(String dbDevId);

}