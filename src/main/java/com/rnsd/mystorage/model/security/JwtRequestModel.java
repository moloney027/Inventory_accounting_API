package com.rnsd.mystorage.model.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtRequestModel {

    private String login;
    private String password;

}
