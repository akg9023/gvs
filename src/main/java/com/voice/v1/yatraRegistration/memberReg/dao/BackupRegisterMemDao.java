package com.voice.v1.yatraRegistration.memberReg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.v1.yatraRegistration.memberReg.model.BackupRegisteredMember;

public interface BackupRegisterMemDao extends JpaRepository<BackupRegisteredMember,String> {
}