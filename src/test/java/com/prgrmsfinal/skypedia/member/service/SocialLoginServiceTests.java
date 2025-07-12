package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SocialLoginServiceTests {
    @Mock
    private MemberService memberService;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private SocialLoginServiceImpl socialLoginService;

    @DisplayName("[성공] 소셜 인증 :: 네이버 계정으로 로그인")
    @Test
    void authenticate_whenNaverLoginSucceed_thenReturnSigInInfo() {
        Map<String, Object> response = new HashMap<>() {{
            put("id", "32742776");
            put("name", "홍길동");
            put("email", "honggildong@naver.com");
        }};

        Map<String, Object> attributes = new HashMap<>() {{
            put("response", response);
        }};

        MemberResponseDto.Info info = MemberResponseDto.Info.builder()
                .id(1L)
                .nickname("honggil1234")
                .photoUrl(null)
                .roleTypes(List.of(RoleType.USER, RoleType.ADMIN))
                .build();

        when(memberService.getInfo(anyString())).thenReturn(info);
        when(jwtTokenService.issueAccessToken(anyLong(), anyList())).thenReturn("accessToken");
        doNothing().when(jwtTokenService).issueRefreshToken(anyLong());

        MemberResponseDto.SignIn result = socialLoginService.authenticate(attributes, SocialType.NAVER);

        assertEquals(info.nickname(), result.nickname());
        assertEquals(info.photoUrl(), result.photoUrl());
        assertNotNull(result.accessToken());

        verify(memberService).getInfo(anyString());
        verify(memberService, never()).create(any(MemberRequestDto.SocialInfo.class));
        verify(jwtTokenService).issueAccessToken(anyLong(), anyList());
        verify(jwtTokenService).issueRefreshToken(anyLong());
    }

    @DisplayName("[성공] 소셜 인증 :: 카카오 계정으로 회원가입")
    @Test
    void authenticate_whenKakaoLoginSucceed_thenReturnSignInfo() {
        Map<String, Object> kakaoAccount = new HashMap<>() {{
            put("name", "홍길동");
            put("email", "honggildong@daum.net");
        }};

        Map<String, Object> attributes = new HashMap<>() {{
            put("id", "135246357");
            put("kakao_account", kakaoAccount);
        }};

        MemberResponseDto.Info info = MemberResponseDto.Info.builder()
                .id(1L)
                .nickname("honggil1234")
                .photoUrl("photoUrl")
                .roleTypes(List.of(RoleType.USER, RoleType.ADMIN))
                .build();

        when(memberService.getInfo(anyString())).thenThrow(MemberNotFoundException.class);
        when(memberService.create(any(MemberRequestDto.SocialInfo.class))).thenReturn(info);
        when(jwtTokenService.issueAccessToken(anyLong(), anyList())).thenReturn("accessToken");
        doNothing().when(jwtTokenService).issueRefreshToken(anyLong());

        MemberResponseDto.SignIn result = socialLoginService.authenticate(attributes, SocialType.KAKAO);

        assertEquals(info.nickname(), result.nickname());
        assertEquals(info.photoUrl(), result.photoUrl());
        assertEquals(info.photoUrl(), result.photoUrl());

        verify(memberService).getInfo(anyString());
        verify(memberService).create(any(MemberRequestDto.SocialInfo.class));
        verify(jwtTokenService).issueAccessToken(anyLong(), anyList());
        verify(jwtTokenService).issueRefreshToken(anyLong());
    }

    @DisplayName("[실패] 소셜 인증 :: 구글 로그인 데이터 누락")
    @Test
    void authenticate_whenGoogleLoginSucceed_thenReturnSignInfo() {
        Map<String, Object> attributes = new HashMap<>();

        assertThrows(NullPointerException.class, () -> socialLoginService.authenticate(attributes, SocialType.GOOGLE));
    }
}
