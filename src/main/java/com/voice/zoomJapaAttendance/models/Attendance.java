package com.voice.zoomJapaAttendance.models;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "attendance_devotee_id", "date" }) })
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @ManyToOne
    AttendanceDevotee attendanceDevotee;

    //attendance
    private String meetingName;
    private Integer totalDuration;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
    private LocalDate date;

    @Column( updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;
 
    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
   
}

