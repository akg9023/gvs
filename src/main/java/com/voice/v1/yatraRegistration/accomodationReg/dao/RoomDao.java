package com.voice.v1.yatraRegistration.accomodationReg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.v1.yatraRegistration.accomodationReg.model.RoomType;

public interface RoomDao extends JpaRepository<RoomType,Long> {
    public RoomType findOneByRoomId(String roomId);
}