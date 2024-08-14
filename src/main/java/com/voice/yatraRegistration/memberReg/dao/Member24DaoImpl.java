package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;



//import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.Member24;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

public class Member24DaoImpl implements Member24DaoCustom  {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Member24> getAllSuccessMemBeforeDate() {
        StoredProcedureQuery findMembers =
                em.createNamedStoredProcedureQuery("getRegisteredMembers24");
        List<Member24> mem = findMembers.getResultList();
        System.out.println(mem.size());
        return mem;
    }

}