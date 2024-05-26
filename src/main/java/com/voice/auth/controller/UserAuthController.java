package com.voice.auth.controller;


import com.voice.auth.model.AuthEnums;
import com.voice.auth.model.UserAuth;
import com.voice.auth.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
public class UserAuthController {
    private static final String USER_AUTH_CLAIM_KEY = "UserAuth";
    @Autowired
    private UserAuthService userAuthService;

    @GetMapping
    public ResponseEntity<UserAuth> getUserAuth(Authentication authentication){
         Optional<UserAuth> user =  userAuthService.getUserAuthFromAuthentication(authentication);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
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