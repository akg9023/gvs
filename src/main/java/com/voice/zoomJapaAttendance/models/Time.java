package com.voice.zoomJapaAttendance.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Time {
    
    LocalDateTime joinDateTime;
    LocalDateTime leaveDateTime;
}
