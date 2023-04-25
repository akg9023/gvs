package com.voice.yatraRegistration.memberReg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voice.yatraRegistration.memberReg.model.BackupRegisteredMember;
import com.voice.yatraRegistration.memberReg.model.RegisteredMember;

public interface BackupRegisterMemDao extends JpaRepository<BackupRegisteredMember,String> {
}   
