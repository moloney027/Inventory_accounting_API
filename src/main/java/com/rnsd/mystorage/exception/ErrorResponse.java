package com.rnsd.mystorage.exception;

import lombok.Getter;

/**
 * Модель с сообщением об ошибке
 */
@Getter
public class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
