package com.voice.auth.dao;


import com.voice.auth.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
    public Optional<UserAuth> findByUserEmail(String userTestEmail);
}