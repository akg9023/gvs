package com.voice.v1.dbRegistration.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.v1.dbRegistration.model.PermittedUsers;

public interface PermittedUsersDao extends  JpaRepository<PermittedUsers, String> {
    public PermittedUsers getByEmail(String email);
}