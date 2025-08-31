package com.voice.attendance.utils;

import com.voice.attendance.models.Status;
import com.voice.attendance.models.ZoomAttendance;
import com.voice.attendance.models.ZoomPastMeetingParticipantsResponse;

import java.time.*;
import java.util.Comparator;
import java.util.List;

public class ZoomAttendanceUtils {
    public static ZoomAttendance createNewAttendance(ZoomAttendance newAttendance,
                                                     List<ZoomPastMeetingParticipantsResponse.Participant> participantDetailList, String email, LocalDate date) {

        ZoomPastMeetingParticipantsResponse.Participant maxDurationParticipantDetail = participantDetailList.stream()
                .filter(a -> a.getUserEmail().equalsIgnoreCase(email))
                .max(Comparator.comparingInt(ZoomPastMeetingParticipantsResponse.Participant::getDuration)).get();


        newAttendance.setParticipantEmail(email);
        newAttendance.setDate(date.atStartOfDay().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate());

        LocalDateTime joinDateTime = Instant.parse(maxDurationParticipantDetail.getJoinTime()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        LocalDateTime leaveDateTime = Instant.parse(maxDurationParticipantDetail.getLeaveTime()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        newAttendance.setJoinLeaveTime(joinDateTime + " - " + leaveDateTime);
        newAttendance.setTotalMinutes(maxDurationParticipantDetail.getDuration() / 60);
        newAttendance.setDuration(calDuration(maxDurationParticipantDetail.getDuration() / 60));
        newAttendance.setStatus(Status.PRESENT);
        newAttendance.setJoinTime(joinDateTime);
        newAttendance.setLeaveTime(leaveDateTime);

        return newAttendance;
    }

    private static String calDuration(Integer minutes) {
        int hours = minutes / 60; // Calculate the hours
        int remainingMinutes = minutes % 60; // Calculate the remaining minutes

        return hours + " hours " + remainingMinutes + " minutes ";

    }

    public static LocalDateTime getISTDateTime(LocalDateTime startTimeUtc) {

        // Tell Java this LocalDateTime is UTC
        ZonedDateTime utcZoned = startTimeUtc.atZone(ZoneOffset.UTC);

        // Convert to IST
        LocalDateTime istTime = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();

        return istTime;
    }
}
