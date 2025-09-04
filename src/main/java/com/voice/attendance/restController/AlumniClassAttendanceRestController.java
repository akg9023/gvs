package com.voice.attendance.restController;

import com.voice.attendance.dao.AlumniClassAttendanceDao;
import com.voice.attendance.models.AlumniClassAttendance;
import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/attendance/alumniClass")
@CrossOrigin(origins = "*")
public class AlumniClassAttendanceRestController {
    @Autowired
    AlumniClassAttendanceDao attendanceDao;

    @PostMapping("/monthlyDetail")
    public List<AlumniClassAttendance> fetchAll(@RequestParam String email,
                                                @RequestParam int month, @RequestParam int year) {

        List<AlumniClassAttendance> presentAttendanceByEmail = attendanceDao.findAllByParticipantsIdWithSpecifcMonth(email, month, year);
        List<LocalDate> availableAttendanceDate = attendanceDao.findAvailableAttendanceDates(month, year)
                .stream()
                .map(java.sql.Date::toLocalDate)
                .toList();

        List<AlumniClassAttendance> fullMonthAttendanceByEmail = new ArrayList<>(presentAttendanceByEmail);

        Set<LocalDate> presentDates = presentAttendanceByEmail.stream()
                .collect(Collectors.groupingBy(AlumniClassAttendance::getDate)).keySet();

        availableAttendanceDate.forEach(a -> {
            if (!presentDates.contains(a)) {
                fullMonthAttendanceByEmail.add(new AlumniClassAttendance(a, Status.ABSENT));
            }
        });

        return fullMonthAttendanceByEmail;
    }

    @PostMapping("/saveAbsentReason")
    public AlumniClassAttendance saveAbsentWithReason(@RequestBody AlumniClassAttendance absentAttendaceWithReason) {
        return attendanceDao.save(absentAttendaceWithReason);
    }


    @PostMapping("/fetchAll")
    public List<JapaAttendance> fetchAll(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceDao.findAllByDate(date);
    }

    @PostMapping("/present")
    public List<JapaAttendance> fetchAllPresent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println(date);
        return attendanceDao.findAllByStatusAndDate(Status.PRESENT, date);
    }

    @PostMapping("/absent")
    public List<JapaAttendance> fetchAllAbsent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceDao.findAllByStatusAndDate(Status.ABSENT, date);
    }

}