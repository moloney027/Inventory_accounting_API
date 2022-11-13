package com.rnsd.mystorage.service.security;

import com.rnsd.mystorage.model.security.JwtAuthenticationModel;
import com.rnsd.mystorage.model.security.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthenticationModel generate(Claims claims) {
        final JwtAuthenticationModel jwtInfoToken = new JwtAuthenticationModel();
        jwtInfoToken.setRole(Role.valueOf(claims.get("role", String.class)));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

}
