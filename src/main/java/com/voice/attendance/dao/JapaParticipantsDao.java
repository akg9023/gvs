package com.voice.attendance.dao;

import com.voice.attendance.models.JapaParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;


public interface JapaParticipantsDao extends JpaRepository<JapaParticipants, Long> {
    public JapaParticipants findOneByEmail(String email);

    @Query(value = "SELECT m.id,m.name FROM JapaParticipants m where m.facId = ?1")
    public Map<Long, String> findAllFacilitesId2(Long facId);

    @Query("SELECT new map(m.email , m.name as name) FROM JapaParticipants m WHERE m.facId = ?1")
    public List<Map<String, Object>> findAllFacilitiesId(Long facId);

}