package com.voice.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${UI_LOGIN_SUCCESS}")
    private String UI_HOME;

    @Value("${UI_BASE_DOMAIN}")
    private String UI_PARENT_DOMAIN;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        // Generate a session ID or token (this is a simplified example)
        String sessionId = request.getSession().getId();

        // Create a cookie
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true); // Use true in production
        sessionCookie.setPath("/");
        sessionCookie.setDomain(UI_PARENT_DOMAIN);  // Common domain
        //sessionCookie.setDomain("localhost");
        sessionCookie.setMaxAge(60 * 60 * 24); // 24 hours

        // Add cookie to the response
        response.addCookie(sessionCookie);

        // Redirect or return success response
        response.sendRedirect("http://localhost:3000");
        //response.sendRedirect("http://localhost:3000/v1");

//        this.setAlwaysUseDefaultTargetUrl(true);
//        this.setDefaultTargetUrl(UI_HOME);
////        this.setDefaultTargetUrl("https://localhost:8443/");
//        super.onAuthenticationSuccess(request, response, authentication);
    }
}