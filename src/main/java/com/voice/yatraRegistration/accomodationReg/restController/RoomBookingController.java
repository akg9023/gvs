package com.voice.yatraRegistration.accomodationReg.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomType;

@RestController
@RequestMapping("/v1/room/bookings/")
@CrossOrigin("*")
public class RoomBookingController {

    @Autowired
    RoomBookingDao bookingDao;

    @PostMapping("/fetchAll")
    public List<RoomBooking> fetchAllBookings(){
        return bookingDao.findAll();
    }

    @PostMapping("/saveBooking")
    public RoomBooking saveRoom(@RequestBody RoomBooking booking){
        return bookingDao.save(booking);
    }

}
