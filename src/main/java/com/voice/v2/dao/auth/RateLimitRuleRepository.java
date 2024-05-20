package com.voice.v2.dao.auth;


import com.voice.v2.model.auth.RateLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, String> {
}