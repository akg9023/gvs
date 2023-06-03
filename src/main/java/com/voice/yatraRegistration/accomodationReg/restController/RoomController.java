package com.voice.yatraRegistration.accomodationReg.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.yatraRegistration.accomodationReg.dao.RoomDao;
import com.voice.yatraRegistration.accomodationReg.model.Room;

@RestController
@RequestMapping("/v1/accomodation/rooms/")
@CrossOrigin("*")
public class RoomController {

    @Autowired
    RoomDao roomDao;

    @PostMapping("/fetchAll")
    public List<Room> fetchAllRoom(){
        return roomDao.findAll();
    }

    @PostMapping("/saveRoom")
    public Room saveRoom(@RequestBody Room room){
        return roomDao.save(room);
    }

    @DeleteMapping("/deleteAll")
    public void del(){
         roomDao.deleteAll();
    }
}
