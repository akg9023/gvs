package com.voice.attendance.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ZoomPastMeetingParticipantsResponse {

    @JsonProperty("page_count")
    private int pageCount;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_records")
    private int totalRecords;

    @JsonProperty("next_page_token")
    private String nextPageToken;

    private List<Participant> participants;

    @Data
    public static class Participant {
        private String id;

        @JsonProperty("user_id")
        private String userId;

        private String name;

        @JsonProperty("user_email")
        private String userEmail;

        @JsonProperty("join_time")
        private String joinTime;

        @JsonProperty("leave_time")
        private String leaveTime;

        private int duration;

        @JsonProperty("registrant_id")
        private String registrantId;

        private boolean failover;
        private String status;
        private String groupId;

        @JsonProperty("internal_user")
        private boolean internalUser;
    }
}
