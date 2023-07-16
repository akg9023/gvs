package com.voice.attendance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.Status;


public interface JapaAttendanceDao extends JpaRepository<JapaAttendance,Long> {

    List<JapaAttendance> findAllByStatus(Status status);
}
