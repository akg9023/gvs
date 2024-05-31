package com.voice.auth.controller;


import com.voice.auth.dao.RoleRepository;
import com.voice.auth.model.AuthEnums;
import com.voice.auth.model.Role;
import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import com.voice.dbRegistration.dao.DevoteeInfoDao;
import com.voice.dbRegistration.dao.PermittedUsersDao;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.model.PermittedUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
    Logger logger = LoggerFactory.getLogger(UserAuthController.class);

    private static final String USER_AUTH_CLAIM_KEY = "UserAuth";
    private static final String ROLE_PREFIX = "ROLE_";
    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private PermittedUsersDao permittedUsersDao;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DevoteeInfoDao devoteeInfoDao;

    @GetMapping
    public ResponseEntity<UserAuth> getUserAuth(Authentication authentication){
         Optional<UserAuth> user =  userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    @GetMapping("/migrate")
    public  List<UserAuth> migrate(){
       List<UserAuth> userAuthList =   migratePermittedUserToUserAuth();
       return userAuthService.saveAllUsers(userAuthList);
    }
    @GetMapping("/migrateCheck")
    public List<UserAuth> migratePermittedUserToUserAuth(){
        List<UserAuth> userAuthList = new ArrayList<>();
        try {
            List<PermittedUsers> permittedUsersList = permittedUsersDao.findAll();
            Role role = roleRepository.findByName();
            if(role == null){
                role = new Role();
                role.setName(ROLE_PREFIX + "USER");
                // currently no privileges
                role.setPrivileges(new HashSet<>());

                role = roleRepository.save(role);
                logger.info("Role created id = {},name = {} ",role.getId(),role.getName());
            }


            for (PermittedUsers permittedUsers : permittedUsersList) {
                UserAuth userAuth = new UserAuth();
                userAuth.setUserEmail(permittedUsers.getEmail());
                DevoteeInfo devoteeInfo = devoteeInfoDao.findByEmailAndConnectedTo(userAuth.getUserEmail(),"guru");
                if(devoteeInfo!=null){
                    userAuth.setUserId(devoteeInfo.getId());
                    userAuth.setUserName(devoteeInfo.getFname()+devoteeInfo.getLname());
                }
                userAuth.setVerified(false);
                userAuth.setAccountStatus(AuthEnums.AccountStatus.ACTIVE);
                LocalDateTime currentDateTime = LocalDateTime.now();
                userAuth.setRegistrationDate(currentDateTime);
                userAuth.setTwoFaEnabled(false);
                userAuth.setRoles(Set.of(role));

                userAuthList.add(userAuth);

            }

        }catch (Exception e){
            logger.error("Permitted User migration to User Auth Error {}", e.getMessage());
        }
        return userAuthList;
    }

    /**
     * Note: Creating a user with SuperAdmin role is not allowed
     * SuperAdmin will be able to create admin, SuperUser, User
     * Admin will be able to create SuperUser and User
     * User, or SuperUser cannot create any kind of user
     * @param userAuth
     * @param authentication
     * @return Created User
     */
//    @PostMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('SUPER_ADMIN')")
//    public ResponseEntity<UserAuth> createUserAuth(@RequestBody UserAuth userAuth, Authentication authentication){
//        if(authentication!=null){
//            if(userAuth.getUserRole().equals(AuthEnums.Roles.ROLE_SUPER_ADMIN)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//            if(authentication.getPrincipal() instanceof DefaultOidcUser oidcUser){
//                Map<String, Object> claims = oidcUser.getUserInfo().getClaims();
//                if(claims.containsKey(USER_AUTH_CLAIM_KEY)) {
//                    UserAuth userSession = (UserAuth) claims.get(USER_AUTH_CLAIM_KEY);
//                }
//            }
////            if(userAuthService.validateAuthUserFields(userAuth)){
////                userAuth = userAuthService.saveUserAuth(userAuth);
////                if(userAuth!=null){
////                    ResponseEntity.ok(userAuth);
////                }
////            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//    }
}