package com.rnsd.mystorage.model.security;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель для отправки данных при авторизации пользователя
 */
@Setter
@Getter
public class JwtRequestModel {

    private String login;
    private String password;

}
