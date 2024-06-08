package com.voice.auth.service;


import com.voice.auth.dao.RoleRepository;
import com.voice.auth.dao.UserAuthRepository;
import com.voice.auth.model.AuthEnums;
import com.voice.auth.model.DevoteeInfoMigration;
import com.voice.auth.model.Role;
import com.voice.auth.model.UserAuth;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.dao.PermittedUsersDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.model.PermittedUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAuthService {
    private static final String USER_AUTH_CLAIM_KEY = "UserAuth";
    Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private PermittedUsersDao permittedUsersDao;
    @Autowired
    private DevoteeInfoDao devoteeInfoDao;
    @Autowired
    private RoleRepository roleRepository;

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
    public Optional<List<UserAuth>> migratePermittedUsersToUserAuthCheck(){
      try{
          List<PermittedUsers> permittedUsersList =  permittedUsersDao.findAll();
          List<UserAuth> userAuthList = new ArrayList<>();
          Role role = roleRepository.findByName("ROLE_USER");
          for(PermittedUsers user: permittedUsersList){
              UserAuth userAuth = new UserAuth();
              DevoteeInfoMigration devoteeInfo = devoteeInfoDao.getDevoteeInfoForUserAuth(user.getEmail());
              if(devoteeInfo!=null){
                  userAuth.setUserEmail(user.getEmail());
                  userAuth.setUserId(devoteeInfo.getId());
                  userAuth.setUserName(devoteeInfo.getFname());
                  userAuth.setVerified(false);
                  userAuth.setTwoFaEnabled(false);
                  userAuth.setRoles(Set.of(role));
                  userAuth.setRegistrationDate(devoteeInfo.getCreatedDateTime());
                  userAuth.setAccountStatus(AuthEnums.AccountStatus.ACTIVE);
                  userAuthList.add(userAuth);
              }else{
                  userAuth.setUserName("");
                  userAuth.setUserEmail(user.getEmail());
                  userAuth.setVerified(false);
                  userAuth.setTwoFaEnabled(false);
                  userAuth.setRoles(Set.of(role));
                  userAuth.setAccountStatus(AuthEnums.AccountStatus.ACTIVE);
                  userAuthList.add(userAuth);
              }
          }
          return Optional.of(userAuthList);
      }catch (Exception e){
          logger.error("Migrate Permitted Users to UserAuth Error {}",e.getMessage());
      }
       return Optional.empty();
    }
    public Optional<List<UserAuth>> migratePermittedUsersToUserAuth(){
        Optional<List<UserAuth>> res = migratePermittedUsersToUserAuthCheck();
        if(res.isPresent() && !res.get().isEmpty()){
            return Optional.of(userAuthRepository.saveAll(res.get()));
        }
        return Optional.empty();
    }
    public UserAuth saveUserWithEmailOnly(String email){
        try{
            UserAuth userAuth = new UserAuth();
            Role role = roleRepository.findByName("ROLE_USER");
            userAuth.setUserName("");
            userAuth.setUserEmail(email);
            userAuth.setVerified(false);
            userAuth.setTwoFaEnabled(false);
            userAuth.setRoles(Set.of(role));
            userAuth.setAccountStatus(AuthEnums.AccountStatus.ACTIVE);
            return userAuthRepository.save(userAuth);
        }catch (Exception e){
            logger.error("Save user with email only error {}",e.getMessage());
        }
        return null;
    }
}