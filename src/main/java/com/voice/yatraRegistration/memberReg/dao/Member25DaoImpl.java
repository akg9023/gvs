package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;



//import com.voice.yatraRegistration.memberReg.model.Member;
import com.voice.yatraRegistration.memberReg.model.Member25;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

public class Member25DaoImpl implements Member25DaoCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Member25> getAllSuccessMemBeforeDate() {
        StoredProcedureQuery findMembers =
                em.createNamedStoredProcedureQuery("getRegisteredMembers25");
        List<Member25> mem = findMembers.getResultList();
        System.out.println(mem.size());
        return mem;
    }

}