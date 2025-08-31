package com.voice.attendance.restController;

import com.amazonaws.services.kms.model.NotFoundException;
import com.voice.attendance.dao.AlumniClassAttendanceDao;
import com.voice.attendance.dao.JapaAttendanceDao;
import com.voice.attendance.models.*;
import com.voice.attendance.service.ZoomServiceProvider;
import com.voice.attendance.utils.ZoomAttendanceUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/attendance/japa")
@CrossOrigin(origins = "*")
@Log4j2
public class ZoomAttendanceController {

    @Autowired
    @Lazy
    JapaAttendanceDao japaAtendanceDao;

    @Autowired
    @Lazy
    AlumniClassAttendanceDao alumniAttendanceDao;

    @Value("${ZOOM_JAPA_MEETING_ID}")
    String zoomJapaMeetingId;

    @Value("${ZOOM_ALUMNI_CLASS_MEETING_ID}")
    String zoomAlumniClassMeetingId;

    @Autowired
    ZoomServiceProvider zoomServiceProvider;

    @PostMapping("/update")
    public AttendanceResponse updateZoomMeetings(@RequestParam String zoomMeetingId, @RequestParam LocalDate date) throws Exception {
        int numOfRecords = 0;
        try {

            List<ZoomPastMeetingInstanceResponse.Meeting> pastMeetingInstanceList = zoomServiceProvider
                    .getPastMeetingInstance(zoomMeetingId, date.atStartOfDay());
            if (pastMeetingInstanceList.isEmpty()) {
                throw new NotFoundException("No past meetings found for date: " + date.atStartOfDay());
            }
            String meetingUUID = zoomServiceProvider
                    .getMeetingDetailsHavingMaxParticipantsMeetingUUID(pastMeetingInstanceList).orElseThrow(() -> new IllegalArgumentException("Something went wrong while finding the max participants"));
            List<ZoomPastMeetingParticipantsResponse.Participant> meetingParticipants = zoomServiceProvider
                    .getMeetingParticipants(meetingUUID).orElseThrow(() -> new NotFoundException("No participants found for meeting UUID: " + meetingUUID));
            Set<String> listDistinctEmail = meetingParticipants.stream()
                    .collect(Collectors.groupingBy(ZoomPastMeetingParticipantsResponse.Participant::getUserEmail, Collectors.counting())).keySet();

            // creating object and saving to db
            for (String email : listDistinctEmail) {
                if (email.isEmpty())
                    continue;
                numOfRecords++;

                if (zoomMeetingId.equalsIgnoreCase(zoomJapaMeetingId))
                    japaAtendanceDao.save((JapaAttendance) ZoomAttendanceUtils
                            .createNewAttendance(new JapaAttendance(), meetingParticipants, email, date));
                else if (zoomMeetingId.equalsIgnoreCase(zoomAlumniClassMeetingId))
                    alumniAttendanceDao.save((AlumniClassAttendance) ZoomAttendanceUtils
                            .createNewAttendance(new AlumniClassAttendance(), meetingParticipants, email, date));
                else {
                    return new AttendanceErrorResponse("Zoom meeting id: " + zoomMeetingId + " is not registered.");
                }

            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return new AttendanceErrorResponse(e.getMessage());
        }

        return new AttendanceSuccessResponse("Successfully updated. TotalRecords : " + numOfRecords);
    }


}
