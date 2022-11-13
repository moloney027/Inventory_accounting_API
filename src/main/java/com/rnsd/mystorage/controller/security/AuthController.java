package com.rnsd.mystorage.controller.security;

import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.model.security.RefreshJwtRequestModel;
import com.rnsd.mystorage.service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     *
     */
    @PostMapping("login")
    public ResponseEntity<JwtResponseModel> login(@RequestBody JwtRequestModel authRequest) {
        final JwtResponseModel token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    /**
     *
     */
    @PostMapping("token")
    public ResponseEntity<JwtResponseModel> getNewAccessToken(@RequestBody RefreshJwtRequestModel request) {
        final JwtResponseModel token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /**
     *
     */
    @PostMapping("refresh")
    public ResponseEntity<JwtResponseModel> getNewRefreshToken(@RequestBody RefreshJwtRequestModel request) {
        final JwtResponseModel token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

}
