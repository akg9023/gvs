package com.voice.yatraRegistration.memberReg.dao;

import com.voice.yatraRegistration.memberReg.model.BackupRegisteredMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRegisterMemDao extends JpaRepository<BackupRegisteredMember,String> {
}