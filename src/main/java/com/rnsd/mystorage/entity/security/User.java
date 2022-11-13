package com.rnsd.mystorage.entity.security;

import com.rnsd.mystorage.model.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "Пользователь")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "public", name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID пользователя")
    private Long id;

    @NotBlank
    @Schema(description = "Логин")
    @Column(unique = true)
    private String login;

    @NotBlank
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Пароль")
    private String password;

    @NotBlank
    @Schema(description = "Имя")
    private String firstName;

    @NotBlank
    @Schema(description = "Фамилия")
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Роль")
    private Role role;

    @NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Ключ для пароля")
    private String passwordSecret;
}
