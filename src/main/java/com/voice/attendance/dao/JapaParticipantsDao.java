//package com.voice.attendance.dao;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import com.voice.attendance.models.JapaParticipants;
//
//
//public interface JapaParticipantsDao extends JpaRepository<JapaParticipants,Long> {
//    public JapaParticipants findOneByEmail(String email);
//
//    @Query(value = "SELECT m.id,m.name FROM JapaParticipants m where m.facId = ?1")
//    public Map<Long,String> findAllFacilitesId2(Long facId);
//
//    @Query("SELECT new map(m.id as id, m.name as name) FROM JapaParticipants m WHERE m.facId = ?1")
//public List<Map<String, Object>> findAllFacilitiesId(Long facId);
//
//}