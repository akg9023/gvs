package com.voice.attendance.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.Status;


public interface JapaAttendanceDao extends JpaRepository<JapaAttendance,Long> {

    List<JapaAttendance> findAllByStatus(Status status);
     List<JapaAttendance> findAllByStatusAndDate(Status status,LocalDate date);
     List<JapaAttendance> findAllByDate(LocalDate date);
}
