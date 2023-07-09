package com.voice.zoomJapaAttendance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.zoomJapaAttendance.models.AttendanceDevotee;

public interface AttendanceDevoteeDao extends JpaRepository<AttendanceDevotee,Long> {
    public AttendanceDevotee findOneByEmail(String email);
}
