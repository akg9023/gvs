package com.voice.zoomJapaAttendance.restController;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.voice.zoomJapaAttendance.dao.AttendanceDao;
import com.voice.zoomJapaAttendance.models.Attendance;

@RestController
public class AttendanceRestController {
    @Autowired
    AttendanceDao attendanceDao;

    @PostMapping("/testAtt")
    public void updateAttendance(@RequestBody MultipartFile file) throws EncryptedDocumentException, IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
        int count = 1;
        String meetingId=null;
        for (Row row : sheet) {
            if(count ==2){
                meetingId = String.valueOf(row.getCell(0).getNumericCellValue());
            }
            if (count < 4) {
                count++;
                continue;
            }
            String excel_name = "";
            String excel_email = "";
            Integer excel_duration = 0;
            String excel_isGuest = "";
            for (int i = 0; i < row.getLastCellNum(); i++) {

                if (row.getCell(i) == null)
                    continue;
                switch (i) {
                    case 0: // name
                        excel_name = row.getCell(i).getStringCellValue();
                        break;
                    case 1: // email
                        excel_email = row.getCell(i).getStringCellValue();
                        break;
                    case 2:// duration
                        Double temp = row.getCell(i).getNumericCellValue();
                        excel_duration = temp.intValue();
                        break;
                    case 3: // isGuest
                        excel_isGuest = row.getCell(i).getStringCellValue();
                        break;
                }

            }
            // Attendance attendance = new Attendance(excel_name, excel_email, excel_duration, excel_isGuest);
            Attendance attendance = new Attendance();
            attendance.setMeetingId(meetingId);
            attendance.setEmail(excel_email);
            attendance.setTotalDuration(excel_duration);
            attendance.setName(excel_name);
            if(count==10)
             attendanceDao.save(attendance);
            System.out.println(attendance);
        }

    }

}
