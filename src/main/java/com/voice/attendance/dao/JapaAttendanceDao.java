package com.voice.attendance.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.JapaParticipants;
import com.voice.attendance.models.Status;


public interface JapaAttendanceDao extends JpaRepository<JapaAttendance,Long> {

    List<JapaAttendance> findAllByStatus(Status status);
     List<JapaAttendance> findAllByStatusAndDate(Status status,LocalDate date);
     List<JapaAttendance> findAllByDate(LocalDate date);
     List<JapaAttendance> findAllByJapaParticipants(JapaParticipants japaParticipants);

      @Query("SELECT e FROM JapaAttendance e WHERE e.japaParticipants.id = :jpId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
     List<JapaAttendance> findAllByParticipantsIdWithSpecifcMonth(@Param("jpId") Long jpId ,
                                @Param("month") int month, @Param("year") int year);
}
