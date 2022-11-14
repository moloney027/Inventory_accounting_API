package com.rnsd.mystorage.controller.security;

import com.rnsd.mystorage.entity.security.User;
import com.rnsd.mystorage.model.security.Role;
import com.rnsd.mystorage.model.security.UserModel;
import com.rnsd.mystorage.repository.security.UserRepository;
import com.rnsd.mystorage.service.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(
        name = "Взаимодействие с аккаунтами пользователей"
)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Регистрация (создание нового пользователя с ролью USER)",
            description = "Данный endpoint незащищенный. По умолчанию создается пользователь с ролью USER. Другие " +
                    "роли использовать на этом endpoint'е нельзя. Возвращает пользователя."
    )
    @PostMapping("/sign-up")
    public ResponseEntity<UserModel> signup(@Valid @RequestBody UserModel userModel) {
        String secretPasswordKey = authService.generateSecretPasswordKey();
        String encryptPassword = authService.hashPassword(userModel.getPassword(), secretPasswordKey);
        User user = userRepository.save(new User(
                null, userModel.getLogin(), encryptPassword, userModel.getFirstName(),
                userModel.getLastName(), Role.USER, secretPasswordKey
        ));
        return ResponseEntity.ok(new UserModel(user.getLogin(), user.getFirstName(), user.getLastName(), user.getRole()));
    }

    @Operation(
            summary = "Регистрация (создание нового пользователя с указанием роли)",
            description = "Данный endpoint защищенный и доступен только пользователю с ролью ADMIN. Можно создать " +
                    "нового пользователя как с ролью USER, так и с ролью ADMIN. Возвращает пользователя."
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<UserModel> createUserByAdmin(@Valid @RequestBody UserModel userModel) {

        String secretPasswordKey = authService.generateSecretPasswordKey();
        String encryptPassword = authService.hashPassword(userModel.getPassword(), secretPasswordKey);
        User user = userRepository.save(new User(
                null, userModel.getLogin(), encryptPassword, userModel.getFirstName(),
                userModel.getLastName(), userModel.getRole(), secretPasswordKey
        ));
        return ResponseEntity.ok(new UserModel(user.getLogin(), user.getFirstName(), user.getLastName(), user.getRole()));
    }
}
