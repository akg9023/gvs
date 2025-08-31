package com.voice.attendance.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

public class ZoomPastMeetingInstanceResponse {
    private List<Meeting> meetings;

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    @Getter
    @ToString
    @Setter
    public static class Meeting {
        private String uuid;

        @JsonProperty("start_time")
        private LocalDateTime startTime;
    }
}

