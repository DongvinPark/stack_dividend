package com.dayone.model;

import com.dayone.model.constants.MemberEntity;
import lombok.Data;

public class Auth {

    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private String roles;

        public MemberEntity toEntity() {
            return MemberEntity.builder()
                .username(this.username)
                .password(this.password)
                .rolesString(this.roles)
                .build();
        }
    }

}
