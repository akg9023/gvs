package com.voice.registration.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.registration.model.DevoteeInfo;


public interface DevoteeInfoDao extends JpaRepository<DevoteeInfo,String>  {
    public DevoteeInfo findOneByEmail(String email);
        public void deleteAllByEmail(String email);
        boolean existsByEmail(String email);
        List<DevoteeInfo> findAllByConnectedEmail(String email);
}
