package com.rnsd.mystorage.entity.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Связь пользователя с токеном
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRefreshToken {

    @Id
    @NotNull
    private Long userId;

    @NotNull
    private String refreshToken;
}
