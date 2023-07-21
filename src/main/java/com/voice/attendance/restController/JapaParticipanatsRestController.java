package com.voice.attendance.restController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.voice.attendance.dao.JapaParticipantsDao;
import com.voice.attendance.models.JapaParticipants;

@RestController
@RequestMapping("/v1/participants/japa")
@CrossOrigin(origins = "*")
public class JapaParticipanatsRestController {

    @Autowired
    JapaParticipantsDao devDao;

    @PostMapping("/saveDevotee")
    public JapaParticipants saveDevotee(@RequestBody JapaParticipants attendanceDevotee) {
        return devDao.save(attendanceDevotee);
    }

    @PostMapping("/fetchAll")
    public List<JapaParticipants> fetchAll() {
        return devDao.findAll();
    }

    @PostMapping("/saveListDevotee")
    public List<JapaParticipants> saveListDevotee(@RequestBody List<JapaParticipants> listParticipants){
      
        List<JapaParticipants> result = new ArrayList<>();
        for(JapaParticipants jp:listParticipants){
            result.add(devDao.save(jp));
        }

        return result;
        
    }
}
