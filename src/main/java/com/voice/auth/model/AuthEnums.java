package com.voice.auth.model;

public class AuthEnums {
    public enum Roles{
        ROLE_ADMIN,
        ROLE_SUPER_ADMIN,
        ROLE_SUPER_USER,
        ROLE_USER,
    }
    public enum Privileges{
        GURUKUL,
    }
    public enum AccountStatus {
        ACTIVE(1),
        SUSPENDED(2),
        INACTIVE(0);

        private final int value;

        private AccountStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}