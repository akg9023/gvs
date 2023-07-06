package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import com.voice.yatraRegistration.memberReg.model.Member;

public interface MemberDao extends JpaRepository<Member,Long> {

    @Query("SELECT mem FROM Member mem GROUP BY mem.dbDevId HAVING count(*)>1")
    public List<Member> findDuplicates();

    public List<Member> findByDbDevId(String dbDevId);
    
    // @Procedure(procedureName = "get_registered_members")
    // Object getRegisteredMembersByProcedureName();

    
}   
