package com.voice.attendance.dao;

import com.voice.attendance.models.JapaAttendance;
import com.voice.attendance.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface JapaAttendanceDao extends JpaRepository<JapaAttendance,Long> {

    List<JapaAttendance> findAllByStatus(Status status);
     List<JapaAttendance> findAllByStatusAndDate(Status status,LocalDate date);
     List<JapaAttendance> findAllByDate(LocalDate date);
//     List<JapaAttendance> findAllByJapaParticipants(JapaParticipants japaParticipants);

      @Query("SELECT e FROM JapaAttendance e WHERE e.participantEmail = :email AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
     List<JapaAttendance> findAllByParticipantsIdWithSpecifcMonth(@Param("email") String email ,
                                @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT DISTINCT DATE(a.date) FROM JapaAttendance a WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<java.sql.Date> findAvailableAttendanceDates(@Param("month") int month, @Param("year") int year);
}