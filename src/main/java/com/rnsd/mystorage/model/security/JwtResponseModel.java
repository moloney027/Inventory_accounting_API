package com.rnsd.mystorage.model.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Модель для получения токенов
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponseModel {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

}
