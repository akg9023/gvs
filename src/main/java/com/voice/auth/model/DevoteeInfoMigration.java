package com.voice.auth.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DevoteeInfoMigration {
    private String id;
    private String fname;
    private String email;
   private LocalDateTime createdDateTime;

    public DevoteeInfoMigration(String id, String fname, String email, LocalDateTime createdDateTime) {
        this.id = id;
        this.fname = fname;
        this.email = email;
        this.createdDateTime = createdDateTime;
    }
}