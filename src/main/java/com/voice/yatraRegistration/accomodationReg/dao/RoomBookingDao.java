package com.voice.yatraRegistration.accomodationReg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;

public interface RoomBookingDao extends JpaRepository<RoomBooking,Long> {
    public RoomBooking findOneById(Long id);
    public List<RoomBooking> findAllByPaymentStatus(String paymentStatus);
    public List<RoomBooking> findAllByPaymentStatusLike(String paymentStatus);
}
