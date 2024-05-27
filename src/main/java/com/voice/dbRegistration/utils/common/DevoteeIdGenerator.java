package com.voice.dbRegistration.utils.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;

import com.voice.dbRegistration.dao.DevoteeInfoDao;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class DevoteeIdGenerator implements IdentifierGenerator {

    @Autowired
    DevoteeInfoDao devInfo;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {

        String prefix = "HLZ";


        int year = Year.now().getValue() % 100;

        String rs= devInfo.getLastDevId();

        if (!rs.isEmpty()) {
            String idString;
            String temp = rs;
            int id;
                id = Integer.parseInt(temp.substring(5)) + 1;
            idString = String.format("%04d", id);
            String generatedId = prefix + String.valueOf(year) + idString;
            return generatedId;
        }
        else {
            String idString;
            int id;
            id = 1;
            idString = String.format("%04d", id);
            String generatedId = prefix + String.valueOf(year) + idString;
            return generatedId;

        }
    }
}