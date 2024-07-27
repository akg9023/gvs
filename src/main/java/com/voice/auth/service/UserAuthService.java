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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
    public boolean setUserAuthInSessionAuthentication(Authentication authentication,UserAuth userAuth){
        if(authentication!=null){
            if(authentication.getPrincipal() instanceof DefaultOidcUser oidcUser){
                Map<String, Object> claims = oidcUser.getUserInfo().getClaims();
                if(claims.containsKey(USER_AUTH_CLAIM_KEY)) {
                    Map<String, Object> mutableClaims = new HashMap<>(claims);

                    // Update the mutable map
                    mutableClaims.put(USER_AUTH_CLAIM_KEY, userAuth);

                    // Create a new OidcUserInfo with the updated claims
                    OidcUserInfo newUserInfo = new OidcUserInfo(mutableClaims);

                    // If necessary, you may need to wrap oidcUser with a new instance containing updated claims
                    // This is an example and may need adaptation
                    DefaultOidcUser newOidcUser = new DefaultOidcUser(
                            oidcUser.getAuthorities(),
                            oidcUser.getIdToken(),
                            newUserInfo
                            //oidcUser.getNameAttributeKey()
                    );

                    // Update the authentication object if needed
                    Authentication oldAuth =  SecurityContextHolder.getContext().getAuthentication();
                    Authentication newAuth = new PreAuthenticatedAuthenticationToken(newOidcUser,oldAuth.getCredentials(),oldAuth.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    return  true;
                }
            }
        }
        return false;
    }

    public UserAuth saveUserWithEmailOnly(String email){
        try{
            UserAuth userAuth = new UserAuth();
            Role role = roleRepository.findByName("ROLE_USER");
            userAuth.setUserId("");
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

    /**
     *
     * @param DevoteeInfo
     * @return UserAuth
     *
     * When a user login in db registration UI and they register themselves for the first time
     * at that time UserAuth table which only would have email and role needs to be updated with the HLZ id created
     * from devoteeInfo table
     */
    public Optional<UserAuth> updateUserAuthWhenDevoteeInfoSaveSelf(DevoteeInfo devoteeInfo){
        Optional<UserAuth> res = userAuthRepository.findByUserEmail(devoteeInfo.getEmail());
        if(res.isPresent()){
            Set<Role> role;
            UserAuth userAuth = res.get();
            if(userAuth.getRoles()==null || userAuth.getRoles().isEmpty()){
                 role = Set.of(roleRepository.findByName("ROLE_USER"));
            }else{
                role = userAuth.getRoles();
            }

            userAuth.setUserId(devoteeInfo.getId());
            userAuth.setUserName(devoteeInfo.getFname());
            userAuth.setVerified(false);
            userAuth.setTwoFaEnabled(false);
            userAuth.setRoles(role);
            userAuth.setRegistrationDate(devoteeInfo.getCreatedDateTime());
            userAuth.setAccountStatus(AuthEnums.AccountStatus.ACTIVE);

           return Optional.of(userAuthRepository.save(userAuth));
        }else{
            logger.error("User Must be present in UserAuth table {}",devoteeInfo);
        }
        return Optional.empty();
    }

}