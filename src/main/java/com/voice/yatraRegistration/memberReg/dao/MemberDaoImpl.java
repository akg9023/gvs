package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;



import com.voice.yatraRegistration.memberReg.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

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