package com.voice.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class UserAuth {
    /**
     * GV + current timestamp in milliseconds
     */
    @Id
    private String userId;

    @Column(nullable = false)
    private String userName;
    @Column(unique = true, nullable = false)
    private String userEmail;
    /**
     * Role Hierarchy - 1 is top will contain access to all below roles
     *     1. SUPER_ADMIN
     *     2. ADMIN
     *     3. SUPER_USER
     *     4. USER
     */
    private AuthEnums.Roles userRole;
    private List<AuthEnums.Privileges> userPrivileges;

    public UserAuth() {
    }
    public UserAuth(String userId, String email){
        this.userId = userId;
        this.userEmail = email;
    }

    public UserAuth(String userId, String userName, String userEmail, AuthEnums.Roles userRole, List<AuthEnums.Privileges> userPrivileges) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.userPrivileges = userPrivileges;
    }

    public String getUserId() {
        return userId;
    }

    /**
     *userId should not be changed once created
     */
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public AuthEnums.Roles getUserRole() {
        return userRole;
    }

    public void setUserRole(AuthEnums.Roles userRole) {
        this.userRole = userRole;
    }

    public List<AuthEnums.Privileges> getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(List<AuthEnums.Privileges> userPrivileges) {
        this.userPrivileges = userPrivileges;
    }
}