package com.voice.yatraRegistration.accomodationReg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.voice.yatraRegistration.accomodationReg.dao.RoomBookingDao;
import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.RoomBooking;
import com.voice.yatraRegistration.accomodationReg.model.RoomSet;
import com.voice.yatraRegistration.accomodationReg.model.RoomType;
import com.voice.yatraRegistration.accomodationReg.utils.Constants;
import com.voice.yatraRegistration.memberReg.dao.MemberDao;
import com.voice.yatraRegistration.memberReg.model.Member;

@Service
public class RoomBookingService {

    @Autowired
    RoomDao roomDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    RoomBookingDao bookingDao;

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

     public String validateCountAndCalculateAmount(List<RoomSet> listRoomSet) throws Exception {
        Integer totalAmt = 0;
        for (RoomSet x : listRoomSet) {

            // room details
            String roomId = x.getRoomType().getRoomId();
            RoomType room = roomDao.findOneByRoomId(roomId);
            Integer roomPrice = Integer.parseInt(room.getPrice());
            Integer totalRoomMemCount = room.getMemberCount();

            // member details
            List<Member> members = x.getMember();
            Integer adultMemCount = 0;
            for (Member m : members) {
                Member mem = memberDao.findById(m.getId()).get();
                if (Integer.parseInt(mem.getDbDevAge()) > Constants.ADULTAGE) {
                    adultMemCount++;
                }
            }

            if (adultMemCount > totalRoomMemCount) {
                throw new Exception(Constants.ERROR_MEMBER_COUNT_EXCEED + roomId);
            }
            totalAmt = totalAmt + roomPrice;
        }

        return String.valueOf(totalAmt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long reserveRoom(RoomBooking booking,String amount) throws Exception{
            // check room count and give error if not found
            List<RoomSet> rmSetList = booking.getRoomSet();
            Map<String,Integer> roomTypeMap = new HashMap<>();
            for(RoomSet rmSet:rmSetList ){
                RoomType rmType = rmSet.getRoomType();
                roomTypeMap.put(rmType.getRoomId(), roomTypeMap.getOrDefault(rmType.getRoomId(), 0)+1);
            
            }

            Set<String> keys = roomTypeMap.keySet();
            for(String k:keys){
                RoomType dbRoom = roomDao.findOneByRoomId(k);
                if(roomTypeMap.get(k)>dbRoom.getCount()){
                    throw new Exception("Room gets over.");
                } 
            }

            // save in db with INITIATED state
            booking.setAmount(amount);
            // booking.setUpiTxnId(UUID.randomUUID().toString()); //temp value
            booking.setPaymentStatus(Constants.INITIATED);
            RoomBooking bookedRoom = bookingDao.save(booking);

            // decrease the count
            manageRoomCount(booking.getRoomSet(), false);

            return bookedRoom.getId();
    }
}
