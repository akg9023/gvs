package com.voice.attendance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voice.attendance.models.JapaParticipants;


public interface JapaParticipantsDao extends JpaRepository<JapaParticipants,Long> {
    public JapaParticipants findOneByEmail(String email);

    @Query(value = "SELECT m.id FROM JapaParticipants m where m.facId = ?1")
    public List<Long> findAllFacilitesId(Long facId);
}
