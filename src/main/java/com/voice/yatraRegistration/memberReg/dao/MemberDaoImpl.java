package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import com.voice.yatraRegistration.memberReg.model.Member;

public class MemberDaoImpl implements MemberDaoCustom  {

     @PersistenceContext
    private EntityManager em;

    @Override
    public List<Member> getAllSuccessMemBeforeDate() {
       StoredProcedureQuery findMembers =
              em.createNamedStoredProcedureQuery("getRegisteredMembers");
        List<Member> mem = findMembers.getResultList();
        System.out.println(mem.size());
        return mem;
    }
    
}   
