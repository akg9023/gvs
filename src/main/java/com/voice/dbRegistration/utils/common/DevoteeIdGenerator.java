package com.voice.dbRegistration.utils.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevoteeIdGenerator implements IdentifierGenerator {
    Logger logger = LoggerFactory.getLogger(DevoteeIdGenerator.class);
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {

        String prefix = "HLZ";
        Connection connection;
        try {
            connection = session.getJdbcConnectionAccess().obtainConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int year = Year.now().getValue() % 100;

        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("select max(devotee_id)from devotee_info");

            if (rs.next()) {
                String idString;
                String temp = rs.getString(1);
                int id;
                if (temp == null)
                    id = 1;
                else
                    id = Integer.parseInt(temp.substring(5)) + 1;
                idString = String.format("%04d", id);
                String generatedId = prefix + String.valueOf(year) + idString;
                System.out.println(generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            logger.error("DevoteeIdGenerator error {} ",e.getMessage());
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("DevoteeIdGenerator connection close error {} ",e.getMessage());
            }
        }

        return null;
    }
}