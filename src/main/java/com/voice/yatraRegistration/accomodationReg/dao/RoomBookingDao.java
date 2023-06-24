package com.voice.yatraRegistration.accomodationReg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;

public interface RoomBookingDao extends JpaRepository<RoomBooking,Long> {
    public RoomBooking findOneById(Long id);
}
