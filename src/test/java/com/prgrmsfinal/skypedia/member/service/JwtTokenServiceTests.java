package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.member.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTests {
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenPrefix", "refresh_token:");
    }

    @DisplayName("[성공] 액세스 토큰 발급")
    @Test
    void issueAccessToken_whenValidRequest_thenReturnAccessToken() {
        when(jwtTokenUtil.createAccessToken(anyLong(), anyList()))
                .thenReturn("accessToken");

        String result = jwtTokenService.issueAccessToken(1L, List.of(RoleType.USER));

        assertEquals("accessToken", result);
        verify(jwtTokenUtil).createAccessToken(anyLong(), anyList());
    }

    @DisplayName("[성공] 리프레시 토큰 발급")
    @Test
    void issueRefreshToken_whenValidRequest_thenSaveSuccessfully() {
        when(jwtTokenUtil.createRefreshToken(anyLong())).thenReturn("refreshToken");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        jwtTokenService.issueRefreshToken(1L);

        verify(valueOperations).set("refresh_token:1", "refreshToken", 7, TimeUnit.DAYS);
    }

    @DisplayName("[성공] 리프레시 토큰 조회")
    @Test
    void getRefreshToken_whenTokenExists_thenReturnRefreshToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        jwtTokenService.getRefreshToken(1L);

        verify(valueOperations).get("refresh_token:1");
    }

    @DisplayName("[실패] 리프레시 토큰 제거")
    @Test
    void deleteRefreshToken_whenTokenExists_thenDeleteSuccessfully() {
        when(redisTemplate.delete(anyString())).thenReturn(true);

        jwtTokenService.deleteRefreshToken(1L);

        verify(redisTemplate).delete(anyString());
    }
}
