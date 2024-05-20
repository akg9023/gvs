package com.voice.v2.dao.auth;


import com.voice.v2.model.auth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
    public Optional<UserAuth> findByUserEmail(String userTestEmail);
}