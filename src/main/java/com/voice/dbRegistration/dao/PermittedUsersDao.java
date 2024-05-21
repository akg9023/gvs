package com.voice.dbRegistration.dao;

import com.voice.dbRegistration.model.PermittedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermittedUsersDao extends  JpaRepository<PermittedUsers, String> {
    public PermittedUsers getByEmail(String email);
}