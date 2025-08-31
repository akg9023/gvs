package com.voice.attendance.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class ZoomAttendance {

    public ZoomAttendance(LocalDate date, Status status) {
        this.date = date;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "participant_email")
    String participantEmail;

    // attendance
    @Enumerated(EnumType.STRING)
    private Status status;
    private String reason;
    private Integer totalMinutes;
    private String duration;
    private String joinLeaveTime;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
    private LocalDate date;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;
}
