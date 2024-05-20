package com.voice.v2.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RateLimitRule {
    /**
     * Eg. ipAddress, URI+METHOD+EMAIL, URI+METHOD+GLOBAL
     */
    @Id
    private String identifierKey;
    /**
     * Bucket duration in seconds
     */
    @Column(nullable = false)
    private long bucketDuration;
    @Column(nullable = false)
    private long bucketRequests;
    @Column(nullable = false)
    private boolean enforce;

    public RateLimitRule() {
    }

    public RateLimitRule(String identifierKey, int bucketDuration, int bucketRequests, boolean enforce) {
        this.identifierKey = identifierKey;
        this.bucketDuration = bucketDuration;
        this.bucketRequests = bucketRequests;
        this.enforce = enforce;
    }

    public String getIdentifierKey() {
        return identifierKey;
    }

    public void setIdentifierKey(String identifierKey) {
        this.identifierKey = identifierKey;
    }

    public long getBucketDuration() {
        return bucketDuration;
    }

    public void setBucketDuration(int bucketDuration) {
        this.bucketDuration = bucketDuration;
    }

    public long getBucketRequests() {
        return bucketRequests;
    }

    public void setBucketRequests(int bucketRequests) {
        this.bucketRequests = bucketRequests;
    }

    public boolean isEnforce() {
        return enforce;
    }

    public void setEnforce(boolean enforce) {
        this.enforce = enforce;
    }
}