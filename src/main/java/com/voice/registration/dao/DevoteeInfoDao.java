package com.voice.registration.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.registration.model.DevoteeInfoResponse;

public interface DevoteeInfoDao extends JpaRepository<DevoteeInfoResponse,String>  {
}
