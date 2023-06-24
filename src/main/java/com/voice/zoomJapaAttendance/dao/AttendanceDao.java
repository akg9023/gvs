package com.voice.zoomJapaAttendance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.zoomJapaAttendance.models.Attendance;

public interface AttendanceDao extends JpaRepository<Attendance,Long> {
    
}
