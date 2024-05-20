package com.voice.v2.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtils {

    @Value("${ContentSecurityPolicy}")
    private String contentSeucurityPolicy;

    public String getContentSeucurityPolicy() {
        return contentSeucurityPolicy;
    }

    public void setContentSeucurityPolicy(String contentSeucurityPolicy) {
        this.contentSeucurityPolicy = contentSeucurityPolicy;
    }
}