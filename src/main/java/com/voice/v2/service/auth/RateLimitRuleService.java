package com.voice.v2.service.auth;


import com.voice.v2.dao.auth.RateLimitRuleRepository;
import com.voice.v2.model.auth.RateLimitRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitRuleService {

    private final Map<String, RateLimitRule> rateLimitRuleMap = new ConcurrentHashMap<>();
    @Autowired
    RateLimitRuleRepository rateLimitRuleRepository;

    public RateLimitRule saveRateLimitRule(RateLimitRule rateLimitRule) {
        //If new key will create new entry. For already present key will update
        this.rateLimitRuleMap.put(rateLimitRule.getIdentifierKey(), rateLimitRule);
        return rateLimitRuleRepository.save(rateLimitRule);
    }

    public Iterable<RateLimitRule> saveAllRateLimitRule(Iterable<RateLimitRule> rateLimitRules) {
        return rateLimitRuleRepository.saveAll(rateLimitRules);
    }

    public void loadAllRules() {
        for (RateLimitRule rateLimitRule : rateLimitRuleRepository.findAll()) {
            //this.rateLimitRuleMap.put(rateLimitRule.getIdentifier(),rateLimitRule);
            this.rateLimitRuleMap.put(rateLimitRule.getIdentifierKey(), rateLimitRule);
        }

    }

    public Map<String, RateLimitRule> getRateLimitRuleMap() {
        return rateLimitRuleMap;
    }
}