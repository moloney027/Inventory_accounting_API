package com.rnsd.mystorage.model.security;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель для отправки refresh токена
 */
@Getter
@Setter
public class RefreshJwtRequestModel {

    public String refreshToken;

}
