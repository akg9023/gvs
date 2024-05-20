package com.voice.v2.service.auth;


import com.voice.v2.dao.auth.UserAuthRepository;
import com.voice.v2.model.auth.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService {
    Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    @Autowired
    private UserAuthRepository userAuthRepository;

    public UserAuth saveUserAuth(UserAuth UserAuth) {
        return userAuthRepository.save(UserAuth);
    }


    public List<UserAuth> getAllUserAuth() {
        logger.debug("UserTestService Class");
        return (List<UserAuth>) userAuthRepository.findAll();
    }

    public Optional<UserAuth> getUserAuthByEmail(String userEmail) {
        return userAuthRepository.findByUserEmail(userEmail);
    }
//    public UserAuth getUserAuth(){
//        return userTestRepository.
//    }
}