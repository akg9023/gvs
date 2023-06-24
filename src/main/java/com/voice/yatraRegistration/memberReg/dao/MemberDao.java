package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voice.yatraRegistration.memberReg.model.Member;

public interface MemberDao extends JpaRepository<Member,Long> {

    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
    public List<Member> findDuplicates();

    public List<Member> findByDbDevId(String dbDevId);
    
}   
