package com.voice.attendance.utils;

import com.voice.attendance.restController.ZoomAttendanceController;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Component
public class ZoomAttendanceScheduler {

    @Value("${ZOOM_JAPA_MEETING_ID}")
    String zoomJapaMeetingId;

    @Value("${ZOOM_ALUMNI_CLASS_MEETING_ID}")
    String zoomAlumniClassMeetingId;

    @Autowired
    private ZoomAttendanceController zoomAttendanceController;

    @Scheduled(cron = "0 0 12 * * *")
    public void updateZoomMeetingsEvery10Min() throws Exception {

        List<String> meetings = List.of(zoomAlumniClassMeetingId, zoomJapaMeetingId);
        LocalDate today = LocalDate.now();

        meetings.forEach((a) -> {
            try {
                log.info("Running: Scheduled update run for " + a + " at " + today);
                zoomAttendanceController.updateZoomMeetings(a, today);
            } catch (Exception e) {
                log.info("Got exception while running in schedule.");
                throw new RuntimeException(e);
            }
        });

        log.info("Success: Scheduled update run for " + meetings + " at " + today);
    }
}
