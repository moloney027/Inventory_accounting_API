package com.rnsd.mystorage.model.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class UserModel {

    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    @Schema(defaultValue = "USER")
    private Role role;

    public UserModel(String login, String password, String firstName, String lastName, Role role) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public UserModel(String login, String firstName, String lastName, Role role) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
