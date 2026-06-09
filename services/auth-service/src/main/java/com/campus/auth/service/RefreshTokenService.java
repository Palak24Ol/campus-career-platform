package com.campus.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token-expiry-days}")
    private int refreshTokenExpiryDays;

    private static final String KEY_PREFIX = "refresh_token:";

    public String createRefreshToken(UUID userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                KEY_PREFIX + userId, token, Duration.ofDays(refreshTokenExpiryDays));
        return token;
    }

    public boolean validateRefreshToken(UUID userId, String token) {
        String stored = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        return token.equals(stored);
    }

    public Optional<UUID> extractUserIdFromToken(String token) {
        return redisTemplate.keys(KEY_PREFIX + "*")
                .stream()
                .filter(key -> token.equals(redisTemplate.opsForValue().get(key)))
                .findFirst()
                .map(key -> {
                    try {
                        return UUID.fromString(key.substring(KEY_PREFIX.length()));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                });
    }

    public void deleteRefreshToken(UUID userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
