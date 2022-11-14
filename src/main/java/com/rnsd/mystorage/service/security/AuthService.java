package com.rnsd.mystorage.service.security;

import com.rnsd.mystorage.entity.security.User;
import com.rnsd.mystorage.entity.security.UserRefreshToken;
import com.rnsd.mystorage.exception.CustomException;
import com.rnsd.mystorage.model.security.JwtRequestModel;
import com.rnsd.mystorage.model.security.JwtResponseModel;
import com.rnsd.mystorage.repository.security.RefreshTokenRepository;
import com.rnsd.mystorage.repository.security.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

/**
 * Сервис для работы с аутентификацией
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final Random random = new Random();
    private final Integer keyLength = 35;
    private final Integer keyIterationCount = 100;

    /**
     * Авторизация пользователя с проверкой пароля
     */
    public JwtResponseModel login(@NonNull JwtRequestModel authRequest) {
        final User user = userRepository.findByLogin(authRequest.getLogin())
                .orElseThrow(() -> new CustomException("User not found"));
        String encryptPassword = hashPassword(authRequest.getPassword(), user.getPasswordSecret());
        if (user.getPassword().equals(encryptPassword)) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshTokenRepository.save(new UserRefreshToken(user.getId(), refreshToken));
            return new JwtResponseModel(accessToken, refreshToken);
        } else {
            throw new CustomException("Wrong password");
        }
    }

    /**
     * Получение нового access токена взмаен токену с истекшим сроком
     */
    public JwtResponseModel getAccessToken(@NonNull String refreshToken) {

        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            User user = userRepository.findByLogin(login).orElseThrow(() -> new CustomException("User not found"));
            final String saveRefreshToken = refreshTokenRepository.findByUserId(user.getId()).getRefreshToken();
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponseModel(accessToken, null);
            }
        }
        return new JwtResponseModel(null, null);
    }

    /**
     * Получение новых access и refresh токенов взмаен токенам с истекшим сроком
     */
    public JwtResponseModel refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            User user = userRepository.findByLogin(login).orElseThrow(() -> new CustomException("User not found"));
            final String saveRefreshToken = refreshTokenRepository.findByUserId(user.getId()).getRefreshToken();
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshTokenRepository.save(new UserRefreshToken(user.getId(), newRefreshToken));
                return new JwtResponseModel(accessToken, newRefreshToken);
            }
        }
        throw new CustomException("Wrong JWT token");
    }

    /**
     * Генерация ключа для хэширования пароля
     */
    public String generateSecretPasswordKey() {
        byte[] bytes = new byte[keyLength];
        random.nextBytes(bytes);
        return Encoders.BASE64.encode(bytes);
    }

    /**
     * Хэширование пароля
     */
    public String hashPassword(String password, String secretPasswordKey) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Decoders.BASE64.decode(secretPasswordKey),
                    keyIterationCount, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Encoders.BASE64.encode(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CustomException("Cannot hash the password: ", e);
        }
    }

}
