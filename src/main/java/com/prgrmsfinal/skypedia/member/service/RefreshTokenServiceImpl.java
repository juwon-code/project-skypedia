package com.prgrmsfinal.skypedia.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RefreshTokenServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Value("${refresh-token.key}")
    private String keyPrefix;

    @Override
    public void save(Long memberId, String token) {
        redisTemplate.opsForValue().set(keyPrefix + memberId, token, 7, TimeUnit.DAYS);
    }

    @Override
    public String get(Long memberId) {
        return redisTemplate.opsForValue().get(keyPrefix + memberId);
    }

    @Override
    public void delete(Long memberId) {
        redisTemplate.delete(keyPrefix + memberId);
    }
}
