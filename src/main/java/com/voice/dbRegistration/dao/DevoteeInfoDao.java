package com.voice.dbRegistration.dao;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.model.GetIDFnameGender;

public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo, String> {
    public List<DevoteeInfo> findAllByEmail(String email);
    public DevoteeInfo findOneById(String id);

    public void deleteAllByEmail(String email);

    boolean existsByEmail(String email);

    List<DevoteeInfo> findAllByConnectedTo(String Id);

    @Query(value = "SELECT new com.voice.dbRegistration.model.GetIDFnameGender(d.id,d.fname,d.gender) FROM DevoteeInfo d")
    public List<GetIDFnameGender> findAllDev();
}
