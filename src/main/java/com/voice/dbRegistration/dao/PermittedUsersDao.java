package com.voice.dbRegistration.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.dbRegistration.model.PermittedUsers;

public interface PermittedUsersDao extends  JpaRepository<PermittedUsers, String> {
    public PermittedUsers getByEmail(String email);
}
