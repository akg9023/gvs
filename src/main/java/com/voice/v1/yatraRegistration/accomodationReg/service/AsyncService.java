package com.voice.v1.yatraRegistration.accomodationReg.service;
import com.voice.v1.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.v1.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.v1.yatraRegistration.accomodationReg.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voice.v1.yatraRegistration.memberReg.dao.MemberDao;

@Service
public class AsyncService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    RoomBookingDao bookingDao;

    @Autowired
    RoomBookingService roomBookingService;

    @Async
    public void waitAsync(Long id) {
        try {
            Thread.sleep(360000, 0);
            RoomBooking asyncBookedRoom = bookingDao.findOneById(id);

            if (asyncBookedRoom.getPaymentStatus().equals(Constants.INITIATED)) {
                System.out.println("Pending status found!! for id " + asyncBookedRoom.getId()+"deleting...");

                // increase the count after time lapse
                roomBookingService.manageRoomCount(asyncBookedRoom.getRoomSet(), true);

                // update the status in roomBooking table
                asyncBookedRoom.setPaymentStatus(Constants.TIMEOUT);
                bookingDao.save(asyncBookedRoom);
            }

            System.out.println("Async job Completed for roomBooking Id: " + asyncBookedRoom.getId());
        } catch (InterruptedException e) {

        }

    }

}