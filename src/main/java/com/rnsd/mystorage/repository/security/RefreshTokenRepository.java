package com.rnsd.mystorage.repository.security;

import com.rnsd.mystorage.entity.security.UserRefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<UserRefreshToken, Long> {

    UserRefreshToken findByUserId(Long userId);
}
