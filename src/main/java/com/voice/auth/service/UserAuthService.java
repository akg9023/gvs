package com.voice.auth.service;


import com.voice.auth.dao.UserAuthRepository;
import com.voice.auth.model.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserAuthService {
    private static final String USER_AUTH_CLAIM_KEY = "UserAuth";
    Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    @Autowired
    private UserAuthRepository userAuthRepository;

    public UserAuth saveUserAuth(UserAuth UserAuth) {
        return userAuthRepository.save(UserAuth);
    }
    public List<UserAuth> saveAllUsers(List<UserAuth> userAuthList){
        return userAuthRepository.saveAll(userAuthList);
    }

    public List<UserAuth> getAllUserAuth() {
        logger.debug("UserTestService Class");
        return (List<UserAuth>) userAuthRepository.findAll();
    }

    public Optional<UserAuth> getUserAuthByEmail(String userEmail) {

        return userAuthRepository.findByUserEmail(userEmail);
    }
    public Optional<UserAuth> getUserAuthFromAuthentication(Authentication authentication){
        if(authentication!=null){
            if(authentication.getPrincipal() instanceof DefaultOidcUser oidcUser){
                Map<String, Object> claims = oidcUser.getUserInfo().getClaims();
                if(claims.containsKey(USER_AUTH_CLAIM_KEY)) {
                    return  Optional.of((UserAuth)claims.get(USER_AUTH_CLAIM_KEY));
                }
            }
        }
        return Optional.empty();
    }
}