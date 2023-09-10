package com.laudog.domain.user;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@JsonTypeName("user")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class UserDto {

    protected String id;
    private String email;
    private String token;
    private String username;
    private String password;
    private String bio;
    private String image;
    private List<String> roles;


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonTypeName("user")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    public static class Registration {

        private String username;


        private String email;


        private String password;

        private List<String> roles;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonTypeName("user")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    public static class Login {

        private String email;

        private String password;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @JsonTypeName("user")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    public static class Update {
        private Long id;
        private String email;
        private String username;
        private String bio;
        private String image;
        private List<String> roles;

    }

}

