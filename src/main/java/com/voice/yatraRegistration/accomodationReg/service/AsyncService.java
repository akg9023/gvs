package com.voice.yatraRegistration.accomodationReg.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomSet;
import com.voice.yatraRegistration.accomodationReg.model.RoomType;
import com.voice.yatraRegistration.accomodationReg.utils.Constants;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;

@Service
public class AsyncService {

    @Autowired
    RoomDao roomDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    RoomBookingDao bookingDao;

    @Async
    public void waitAsync(Long id) {
        try {
            Thread.sleep(40000, 0);
            RoomBooking asyncBookedRoom = bookingDao.findOneById(id);

            if (asyncBookedRoom.getPaymentStatus().equals(Constants.INITIATED)) {
                System.out.println("Pending status found!! for id " + asyncBookedRoom.getId());

                // increase the count after time lapse
                manageRoomCount(asyncBookedRoom.getRoomSet(), true);

                // update the status in roomBooking table
                asyncBookedRoom.setPaymentStatus(Constants.TIMEOUT);
                bookingDao.save(asyncBookedRoom);
            }

            System.out.println("Async job Completed for roomBooking Id: " + asyncBookedRoom.getId());
        } catch (InterruptedException e) {

        }

    }

    public void manageRoomCount(List<RoomSet> listRoomSet, boolean toIncrease) {
        for (RoomSet x : listRoomSet) {
            String roomId = x.getRoomType().getRoomId();
            RoomType room = roomDao.findOneByRoomId(roomId);
            if (toIncrease)
                room.setCount(room.getCount() + 1);
            else
                room.setCount(room.getCount() - 1);
            roomDao.save(room);
        }
    }

}
