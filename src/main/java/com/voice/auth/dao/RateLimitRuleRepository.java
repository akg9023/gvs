package com.voice.auth.dao;


import com.voice.auth.model.RateLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, String> {
}