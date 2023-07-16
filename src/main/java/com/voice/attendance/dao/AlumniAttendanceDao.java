package com.voice.attendance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.attendance.models.AlumniClassAttendance;
import com.voice.attendance.models.JapaAttendance;


public interface AlumniAttendanceDao extends JpaRepository<AlumniClassAttendance,Long> {
    
}
