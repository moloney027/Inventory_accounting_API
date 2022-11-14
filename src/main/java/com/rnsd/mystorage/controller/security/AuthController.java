package com.rnsd.mystorage.controller.security;

import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.model.security.RefreshJwtRequestModel;
import com.rnsd.mystorage.service.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Токены",
        description = "Работа с токенами"
)
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Необходимо ввести логин и пароль существующего пользователя, в результате чего будут " +
                    "получены два токена (access и refresh). Для работы со всеми защищенными endpoint'ами необходимо " +
                    "использовать полученный access токен."
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> login(@RequestBody JwtRequestModel authRequest) {
        final JwtResponseModel token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Получение нового access токена по refresh токену",
            description = "Незащищенный endpoint на котором можно получить новый access токен по refresh токену."
    )
    @PostMapping("/token")
    public ResponseEntity<JwtResponseModel> getNewAccessToken(@RequestBody RefreshJwtRequestModel request) {
        final JwtResponseModel token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Получение нового access и refresh токена по refresh токену",
            description = "Защищенный endpoint на котором можно получить новые access и refresh токены по refresh " +
                    "токену."
    )
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseModel> getNewRefreshToken(@RequestBody RefreshJwtRequestModel request) {
        final JwtResponseModel token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

}
