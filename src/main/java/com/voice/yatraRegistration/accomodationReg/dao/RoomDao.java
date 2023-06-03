package com.voice.yatraRegistration.accomodationReg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.accomodationReg.model.Room;

public interface RoomDao extends JpaRepository<Room,Long> {
    
}
