package com.voice.zoomJapaAttendance.models;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@NoArgsConstructor
public class Attendance {
    
    @Id
    private String email;
    private String name;
    private String dbId;

    //attendance
    private String meetingId;
    private Integer totalDuration;
    private String joinTime;
    private String leaveTime;
    private Date date;
   
}

