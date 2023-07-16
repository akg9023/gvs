package com.voice.attendance.restController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.server.PathParam;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voice.attendance.Constant.Constant;
import com.voice.attendance.dao.JapaAttendanceDao;
import com.voice.attendance.dao.JapaParticipantsDao;
import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.JapaParticipants;
import com.voice.attendance.models.Status;
import com.voice.attendance.models.Time;

@RestController
@RequestMapping("/v1/attendance/japa")
public class JapaAttendanceRestController {
    @Autowired
    JapaAttendanceDao attendanceDao;

    @Autowired
    JapaParticipantsDao devDao;

    @PostMapping("/fetchAll")
    public List<JapaAttendance> fetchAll(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceDao.findAllByDate(date);
    }

    @PostMapping("/present")
    public List<JapaAttendance> fetchAllPresent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println(date);
        return attendanceDao.findAllByStatusAndDate(Status.PRESENT,date);
    }

    @PostMapping("/absent")
    public List<JapaAttendance> fetchAllAbsent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceDao.findAllByStatusAndDate(Status.ABSENT,date);
    }

    @PostMapping("/update")
    public String updateJoinLeaveTime(@RequestParam MultipartFile file)
            throws EncryptedDocumentException, IOException {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        Map<String, Integer> totalDuration = new HashMap<>();
        Map<String, Time> timeHelper = new HashMap<>();
        String errMsg = "";

        int count = 1;
        for (Row row : sheet) {

            if (count == 1) {
                count++;
                try {
                    boolean isJoinTimeAvail = row.getCell(2).getStringCellValue().contains(Constant.JOIN_TIME);
                    boolean isLeaveTimeAvail = row.getCell(3).getStringCellValue().contains(Constant.LEAVE_TIME);
                    boolean isDurationAvail = row.getCell(4).getStringCellValue().contains(Constant.DURATION);
                    if (!isJoinTimeAvail && !isLeaveTimeAvail && !isDurationAvail) {
                        return "JoinTime / LeaveTime / Duration not found in the file";
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return "Please send the correct file.";
                }
                continue;

            }

            String excelEmail = "";
            String excelJoinTime = null;
            String excelLeaveTime = null;
            int excelDuration = 0;
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i) == null)
                    continue;
                switch (i) {

                    case 1: // email
                        excelEmail = row.getCell(i).getStringCellValue();
                        break;
                    case 2:// joinTime
                        excelJoinTime = row.getCell(i).getStringCellValue();
                        break;
                    case 3: // leaveTime
                        excelLeaveTime = row.getCell(i).getStringCellValue();
                        break;
                    case 4:// duration
                        Double temp = row.getCell(i).getNumericCellValue();
                        excelDuration = temp.intValue();
                        totalDuration.put(excelEmail, totalDuration.getOrDefault(excelEmail, 0) + excelDuration);
                        break;
                }
            }

            Time devTimes = timeHelper.get(excelEmail);
            if (devTimes == null) {
                Time newTimes = new Time(excelJoinTime, excelLeaveTime);
                timeHelper.put(excelEmail, newTimes);
            } else {
                devTimes.setLeaveDateTime(excelLeaveTime);
                timeHelper.put(excelEmail, devTimes);
            }

        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        List<JapaParticipants> allRegJapaParticipants = devDao.findAll();
        String firstKey = timeHelper.keySet().iterator().next();
         LocalDate date = LocalDate.parse(timeHelper.get(firstKey).getJoinDateTime().substring(0, 8), formatter);
         DateTimeFormatter joinLeaveformatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
         for (JapaParticipants jp : allRegJapaParticipants) {
            String email = jp.getEmail().toLowerCase();
            Time devTimes = timeHelper.get(email);
            JapaAttendance newAttendance = new JapaAttendance();
            newAttendance.setJapaParticipants(jp);
            newAttendance.setDate(date);
            if (devTimes != null) {
                String join = timeHelper.get(email).getJoinDateTime().substring(8);
                String leave = timeHelper.get(email).getLeaveDateTime().substring(8);
                newAttendance.setJoinLeaveTime(join + " - " + leave);
                newAttendance.setTotalMinutes(totalDuration.get(email));
                newAttendance.setDuration(calDuration(totalDuration.get(email)));
                newAttendance.setStatus(Status.PRESENT);
                newAttendance.setJoinTime(LocalTime.parse(join.trim(),joinLeaveformatter));
                newAttendance.setLeaveTime(LocalTime.parse(leave.trim(),joinLeaveformatter));

            } else {
                newAttendance.setStatus(Status.ABSENT);
            }

            try {
                attendanceDao.save(newAttendance);
            } catch (Exception e) {
                errMsg = errMsg + "\n Error : Duplicate entry for date : " + date.toString() +" and email : "+ email;
            }
        }

        return "Successfully updated\n" + errMsg ;

    }

    private String calDuration(Integer minutes){
        int hours = minutes / 60; // Calculate the hours
        int remainingMinutes = minutes % 60; // Calculate the remaining minutes

        return hours+" hours "+ remainingMinutes + " minutes ";

    }
}
