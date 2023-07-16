package com.voice.attendance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.attendance.models.JapaParticipants;


public interface JapaParticipantsDao extends JpaRepository<JapaParticipants,Long> {
    public JapaParticipants findOneByEmail(String email);
}
