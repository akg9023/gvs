package com.voice.dbRegistration.dao;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import com.voice.dbRegistration.model.DevoteeInfo;

public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo, String> {
    public DevoteeInfo findOneByEmail(String email);
    public DevoteeInfo findOneById(String id);

    public void deleteAllByEmail(String email);

    boolean existsByEmail(String email);

    List<DevoteeInfo> findAllByConnectedTo(String Id);

    // @Query("SELECT devotee_id,fname,lname,gender FROM devotee_info")
    // List<DevoteeInfo> findAll();
}
