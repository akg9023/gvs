package com.voice.dbRegistration.utils.common;

import java.io.Serializable;
import java.time.Year;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.service.DatabaseService;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class DevoteeIdGenerator implements IdentifierGenerator {

    @Autowired
    private DevoteeInfoDao devoteeInfoDao;

    public String generate(){
        String prefix = "HLZ";


        int year = Year.now().getValue() % 100;

        String rs= devoteeInfoDao.getLastDevId();

        if (!rs.isEmpty()) {
            String idString;
            int id;
            id = Integer.parseInt(rs.substring(5)) + 1;
            idString = String.format("%04d", id);
            return prefix + String.valueOf(year) + idString;
        }
        else {
            String idString;
            int id;
            id = 1;
            idString = String.format("%04d", id);
            return prefix + String.valueOf(year) + idString;

        }
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return null;
    }

//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object)
//            throws HibernateException {
//        String prefix = "HLZ";
//
//
//        int year = Year.now().getValue() % 100;
//
//        String rs= devoteeInfoDao.getLastDevId();
//
//        if (!rs.isEmpty()) {
//            String idString;
//            int id;
//            id = Integer.parseInt(rs.substring(5)) + 1;
//            idString = String.format("%04d", id);
//            return prefix + String.valueOf(year) + idString;
//        }
//        else {
//            String idString;
//            int id;
//            id = 1;
//            idString = String.format("%04d", id);
//            return prefix + String.valueOf(year) + idString;
//
//        }
//
//    }

}