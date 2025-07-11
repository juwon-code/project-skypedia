package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberRequestDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.exception.InvalidSocialTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SocialLoginServiceImpl implements SocialLoginService {
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public SocialLoginServiceImpl(MemberService memberService, JwtTokenService jwtTokenService) {
        this.memberService = memberService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public MemberResponseDto.SignIn authenticate(Map<String, Object> attributes, SocialType socialType) {
        MemberRequestDto.SocialInfo socialInfo = switch (socialType) {
            case NAVER -> ofNaver(attributes);
            case KAKAO -> ofKakao(attributes);
            case GOOGLE -> ofGoogle(attributes);
            default -> throw new InvalidSocialTypeException();
        };

        MemberResponseDto.Info info;

        try {
            info = memberService.getInfo(socialInfo.oauthId());
        } catch (NoSuchElementException e) {
            info = memberService.create(socialInfo);
        }

        String accessToken = jwtTokenService.issueAccessToken(info.id(), info.roleTypes());

        jwtTokenService.issueRefreshToken(info.id());

        return MemberResponseDto.SignIn.builder()
                .nickname(info.nickname())
                .photoUrl(info.photoUrl())
                .accessToken(accessToken)
                .build();
    }

    private MemberRequestDto.SocialInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return MemberRequestDto.SocialInfo.builder()
                .oauthId((String) response.get("id"))
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .socialType(SocialType.NAVER)
                .build();
    }

    private MemberRequestDto.SocialInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

        return MemberRequestDto.SocialInfo.builder()
                .oauthId((String) attributes.get("id"))
                .name((String) account.get("name"))
                .email((String) account.get("email"))
                .socialType(SocialType.KAKAO)
                .build();
    }

    private MemberRequestDto.SocialInfo ofGoogle(Map<String, Object> attributes) {
        return MemberRequestDto.SocialInfo.builder()
                .oauthId((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .socialType(SocialType.GOOGLE)
                .build();
    }
}
