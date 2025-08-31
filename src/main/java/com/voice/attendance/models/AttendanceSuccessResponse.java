package com.voice.attendance.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AttendanceSuccessResponse implements AttendanceResponse {

    private String message;
}
