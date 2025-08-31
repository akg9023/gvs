package com.voice.attendance.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name="zoom_japa_attendance", uniqueConstraints = { @UniqueConstraint(columnNames = { "participant_email", "date" }) })
public class JapaAttendance extends ZoomAttendance {
    public JapaAttendance(LocalDate date, Status status) {
        super(date, status);
    }
}