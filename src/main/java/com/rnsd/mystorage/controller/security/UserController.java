package com.rnsd.mystorage.controller.security;

import com.rnsd.mystorage.entity.security.User;
import com.rnsd.mystorage.model.security.Role;
import com.rnsd.mystorage.model.security.UserModel;
import com.rnsd.mystorage.repository.security.UserRepository;
import com.rnsd.mystorage.service.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<UserModel> signup(@Valid @RequestBody UserModel userModel) {
        String secretPasswordKey = authService.generateSecretPasswordKey();
        String encryptPassword = authService.encryptPassword(userModel.getPassword(), secretPasswordKey);
        User user = userRepository.save(new User(
                null, userModel.getLogin(), encryptPassword, userModel.getFirstName(),
                userModel.getLastName(), Role.USER, secretPasswordKey
        ));
        return ResponseEntity.ok(new UserModel(user.getLogin(),user.getFirstName(), user.getLastName(), user.getRole()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<UserModel> createUserByAdmin(@Valid @RequestBody UserModel userModel) {

        String secretPasswordKey = authService.generateSecretPasswordKey();
        String encryptPassword = authService.encryptPassword(userModel.getPassword(), secretPasswordKey);
        User user = userRepository.save(new User(
                null, userModel.getLogin(), encryptPassword, userModel.getFirstName(),
                userModel.getLastName(), userModel.getRole(), secretPasswordKey
        ));
        return ResponseEntity.ok(new UserModel(user.getLogin(),user.getFirstName(), user.getLastName(), user.getRole()));
    }
}
