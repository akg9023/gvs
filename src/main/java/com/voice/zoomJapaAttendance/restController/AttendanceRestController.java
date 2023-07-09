package com.voice.zoomJapaAttendance.restController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voice.zoomJapaAttendance.Constant.Constant;
import com.voice.zoomJapaAttendance.dao.AttendanceDao;
import com.voice.zoomJapaAttendance.dao.AttendanceDevoteeDao;
import com.voice.zoomJapaAttendance.models.Attendance;
import com.voice.zoomJapaAttendance.models.AttendanceDevotee;
import com.voice.zoomJapaAttendance.models.Time;

@RestController
@RequestMapping("/v1/attendance/excel")
public class AttendanceRestController {
    @Autowired
    AttendanceDao attendanceDao;

    @Autowired
    AttendanceDevoteeDao devDao;

    @PostMapping("/japa/fetchAll")
    public List<Attendance> fetchAll() {
        return attendanceDao.findAll();
    }

    @PostMapping("/japa/update")
    public String updateJoinLeaveTime(@RequestBody MultipartFile file) throws EncryptedDocumentException, IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        Map<String, Integer> totalDuration = new HashMap<>();
        Map<String, Time> timeHelper = new HashMap<>();

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
            LocalDateTime excelJoinTime = null;
            LocalDateTime excelLeaveTime = null;
            int excelDuration = 0;
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i) == null)
                    continue;
                switch (i) {

                    case 1: // email
                        excelEmail = row.getCell(i).getStringCellValue();
                        break;
                    case 2:// joinTime
                        excelJoinTime = row.getCell(i).getLocalDateTimeCellValue();
                        break;
                    case 3: // leaveTime
                        excelLeaveTime = row.getCell(i).getLocalDateTimeCellValue();
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

        for (

        String eId : timeHelper.keySet()) {
            Attendance newAttendance = new Attendance();
            AttendanceDevotee dev = devDao.findOneByEmail(eId);
            if (dev != null) {
                newAttendance.setAttendanceDevotee(dev);
                newAttendance.setMeetingName(Constant.JAPA);
                newAttendance.setJoinTime(timeHelper.get(eId).getJoinDateTime());
                newAttendance.setLeaveTime(timeHelper.get(eId).getLeaveDateTime());
                newAttendance.setTotalDuration(totalDuration.get(eId));
                newAttendance.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));

                try {
                    attendanceDao.save(newAttendance);
                } catch (Exception e) {
                    return "Duplicate entry !! ";
                }

            }

        }

        return "Successfully updated";

    }

    @PostMapping("/japa/overview/")
    public void updateTotalDuration(@RequestBody MultipartFile file) throws EncryptedDocumentException, IOException {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        Attendance newAttendance = new Attendance();

        int count = 1;

        for (Row row : sheet) {

            if (count < 4) {
                count++;
                continue;
            }
            String excelName = "";
            String excelEmail = "";
            Integer excelDuration = 0;
            String excelIsGuest = "";
            for (int i = 0; i < row.getLastCellNum(); i++) {

                if (row.getCell(i) == null)
                    continue;
                switch (i) {
                    case 0: // name
                        excelName = row.getCell(i).getStringCellValue();
                        break;
                    case 1: // email
                        excelEmail = row.getCell(i).getStringCellValue();
                        break;
                    case 2:// duration
                        Double temp = row.getCell(i).getNumericCellValue();
                        excelDuration = temp.intValue();
                        break;
                    case 3: // isGuest
                        excelIsGuest = row.getCell(i).getStringCellValue();
                        break;
                }

            }

            AttendanceDevotee dev = devDao.findOneByEmail(excelEmail);
            if (dev != null) {
                newAttendance.setAttendanceDevotee(dev);
                newAttendance.setMeetingName(Constant.JAPA);
                newAttendance.setTotalDuration(excelDuration);
                newAttendance.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
                attendanceDao.save(newAttendance);
            }

        }

    }

}
