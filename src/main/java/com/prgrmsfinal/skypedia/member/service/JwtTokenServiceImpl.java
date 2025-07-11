package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${refresh-token.key}")
    private String refreshTokenPrefix;

    @Autowired
    public JwtTokenServiceImpl(JwtTokenUtil jwtTokenUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String issueAccessToken(Long memberId, List<RoleType> roleTypes) {
        List<String> roleTypeStrings = roleTypes.stream().map(RoleType::toString).toList();

        return jwtTokenUtil.createAccessToken(memberId, roleTypeStrings);
    }

    @Override
    public void issueRefreshToken(Long memberId) {
        String refreshKey = refreshTokenPrefix + memberId;
        String refreshToken = jwtTokenUtil.createRefreshToken(memberId);

        redisTemplate.opsForValue()
                .set(refreshKey, refreshToken, 7, TimeUnit.DAYS);
    }

    @Override
    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get(refreshTokenPrefix + memberId);
    }

    @Override
    public void deleteRefreshToken(Long memberId) {
        String refreshKey = refreshTokenPrefix + memberId;

        redisTemplate.delete(refreshKey);
    }
}
