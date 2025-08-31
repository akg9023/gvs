package com.voice.attendance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voice.attendance.models.ZoomPastMeetingInstanceResponse;
import com.voice.attendance.models.ZoomPastMeetingParticipantsResponse;
import com.voice.attendance.utils.ZoomAttendanceUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ZoomServiceProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private String zoomOAuthToken = null;

    @Value("${ZOOM_ACCOUNT_ID}")
    private String accountId;

    @Value("${ZOOM_API_CLIENT_ID}")
    private String clientId;

    @Value("${ZOOM_API_CLIENT_SECRET}")
    private String clientSecret;

    private String zoomBaseURL = "https://api.zoom.us";
    private String zoomOAuthURL = "https://zoom.us/oauth/token?grant_type=account_credentials";


    public Optional<List<ZoomPastMeetingParticipantsResponse.Participant>> getMeetingParticipants(String meetingUUID) throws JsonProcessingException {
        String meetingParticipantsURL = UriComponentsBuilder.fromHttpUrl(zoomBaseURL + "/v2/past_meetings/" + meetingUUID + "/participants")
                .queryParam("page_size", 150)
//                .queryParam("next_page_token", nextPageToken)
                .toUriString();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<ZoomPastMeetingParticipantsResponse> pastMeetingInstanceParticipants;
        try {
            pastMeetingInstanceParticipants =
                    restTemplate.exchange(meetingParticipantsURL, HttpMethod.GET, entity, ZoomPastMeetingParticipantsResponse.class);
        } catch (HttpClientErrorException.Unauthorized ex) {
            zoomOAuthToken = null;
            httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
            entity = new HttpEntity<>(httpHeaders);

            // retry once with new token
            pastMeetingInstanceParticipants = restTemplate.exchange(meetingParticipantsURL, HttpMethod.GET, entity, ZoomPastMeetingParticipantsResponse.class);
        }
        return Optional.ofNullable(pastMeetingInstanceParticipants.getBody().getParticipants());
    }

    public Optional<String> getMeetingDetailsHavingMaxParticipantsMeetingUUID(List<ZoomPastMeetingInstanceResponse.Meeting> pastMeetingInstanceList) throws JsonProcessingException {

        int maxParticipants = 0;
        Optional<String> maxParticipantsMeetingUUID = Optional.empty();
        // filter meeting instance which has max number of participants
        for (ZoomPastMeetingInstanceResponse.Meeting meeting : pastMeetingInstanceList) {
            String meetingParticipantsURL = UriComponentsBuilder.fromHttpUrl(zoomBaseURL + "/v2/past_meetings/" + meeting.getUuid())
                    .queryParam("page_size", 150)
//                .queryParam("next_page_token", nextPageToken)  TODO - implement next page
                    .toUriString();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<JsonNode> pastMeetingInstance;
            try {
                pastMeetingInstance =
                        restTemplate.exchange(meetingParticipantsURL, HttpMethod.GET, entity, JsonNode.class);

            } catch (HttpClientErrorException.Unauthorized ex) {
                zoomOAuthToken = null;
                httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
                entity = new HttpEntity<>(httpHeaders);

                // retry once with new token
                pastMeetingInstance = restTemplate.exchange(meetingParticipantsURL, HttpMethod.GET, entity, JsonNode.class);
            } catch (HttpClientErrorException.NotFound ex) {
                // meeting does not exist
                log.debug("Meeting does not exist.");
                continue;
            }
            Optional<Integer> participantsCount = Optional.of(pastMeetingInstance.getBody().get("participants_count").asInt());
            if (participantsCount.orElse(0) > maxParticipants) {
                maxParticipants = participantsCount.get();
                maxParticipantsMeetingUUID = Optional.ofNullable(pastMeetingInstance.getBody().get("uuid").asText());
            }
        }
        return maxParticipantsMeetingUUID;
    }

    public List<ZoomPastMeetingInstanceResponse.Meeting> getPastMeetingInstance(String meetingId, LocalDateTime dateTime) throws JsonProcessingException {

        String pastMeetingInstanceURL = UriComponentsBuilder.fromHttpUrl(zoomBaseURL + "/v2/past_meetings/" + meetingId + "/instances")
                .queryParam("page_size", 150)
//                .queryParam("next_page_token", nextPageToken)
                .toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<ZoomPastMeetingInstanceResponse> pastMeetingInstance;
        try {
            pastMeetingInstance = restTemplate.exchange(pastMeetingInstanceURL, HttpMethod.GET, entity, ZoomPastMeetingInstanceResponse.class);
        } catch (HttpClientErrorException.Unauthorized ex) {
            zoomOAuthToken = null;
            httpHeaders.set("Authorization", "Bearer " + getZoomOAuthToken());
            entity = new HttpEntity<>(httpHeaders);

            // retry once with new token
            pastMeetingInstance = restTemplate.exchange(
                    pastMeetingInstanceURL,
                    HttpMethod.GET,
                    entity,
                    ZoomPastMeetingInstanceResponse.class
            );
        }

        // filter with date
        List<ZoomPastMeetingInstanceResponse.Meeting> listMeetings = pastMeetingInstance.getBody().getMeetings();


        return listMeetings.stream()
                .map(a -> {
                    a.setStartTime(ZoomAttendanceUtils.getISTDateTime(a.getStartTime()));
                    return a;
                })
                .filter(a -> a.getStartTime().isAfter(dateTime) && a.getStartTime().isBefore(dateTime.plusDays(1)))
                .toList();
    }


    private String getZoomOAuthToken() throws JsonProcessingException {

        if (zoomOAuthToken != null)
            return zoomOAuthToken;

        String url = UriComponentsBuilder.fromHttpUrl(zoomOAuthURL)
                .queryParam("account_id", accountId)
                .toUriString();

        String basicAuthHeader = createBasicAuthHeader(clientId, clientSecret);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", basicAuthHeader);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> zoomJwtToken =
                restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (zoomJwtToken.hasBody()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResult = mapper.readTree(zoomJwtToken.getBody());
            zoomOAuthToken = jsonResult.get("access_token").asText();
        } else {
            log.info("No body.");
        }
        return zoomOAuthToken;
    }

    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
