package com.voice.attendance.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name="zoom_alumni_class_attendance", uniqueConstraints = { @UniqueConstraint(columnNames = { "participant_email", "date" }) })
public class AlumniClassAttendance extends ZoomAttendance {
    public AlumniClassAttendance(LocalDate date, Status status) {
        super(date, status);
    }

}