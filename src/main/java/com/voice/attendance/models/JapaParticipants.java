package com.voice.attendance.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@NoArgsConstructor
public class JapaParticipants { // use as global for all attendance also

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true)
    private String email;
    private String name;
    @Column(unique = true)
    private String dbId;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Long facId;

}