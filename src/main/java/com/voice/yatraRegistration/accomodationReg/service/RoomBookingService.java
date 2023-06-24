package com.voice.yatraRegistration.accomodationReg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
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

    public void manageRoomCount(List<RoomSet> listRoomSet, boolean toIncrease) {

        // TODO - check if count is there or not
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
            totalAmt = totalAmt + (roomPrice * adultMemCount);
        }

        return String.valueOf(totalAmt);
    }
}
