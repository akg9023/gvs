package com.voice.attendance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.attendance.models.AlumniClassParticipants;
import com.voice.attendance.models.JapaParticipants;


public interface AlumniParticipantsDao extends JpaRepository<AlumniClassParticipants,Long> {
    public AlumniClassParticipants findOneByEmail(String email);
}
