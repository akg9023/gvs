package com.voice.auth.controller;


import com.voice.auth.dao.RoleRepository;
import com.voice.auth.model.AuthEnums;
import com.voice.auth.model.DevoteeInfoMigration;
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

    @PostMapping("/userWithEmail")
    @PreAuthorize("hasRole('ADMIN')")//hasRole is used that's why ROLE_ prefix not required
    public UserAuth saveUser(@RequestParam String email){
        return userAuthService.saveUserWithEmailOnly(email);
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