package com.voice.zoomJapaAttendance.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voice.zoomJapaAttendance.dao.AttendanceDevoteeDao;
import com.voice.zoomJapaAttendance.models.AttendanceDevotee;

@RestController
@RequestMapping("/v1/attendance/devotee")
public class AttDevoteeController {

    @Autowired
    AttendanceDevoteeDao devDao;

    @PostMapping("/saveDevotee")
    public AttendanceDevotee saveDevotee(@RequestBody AttendanceDevotee attendanceDevotee) {
        return devDao.save(attendanceDevotee);
    }

    @PostMapping("/fetchAll")
    public List<AttendanceDevotee> fetchAll() {
        return devDao.findAll();
    }

}
