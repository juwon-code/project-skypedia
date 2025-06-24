package com.prgrmsfinal.skypedia.member.service;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.member.dto.MemberInternalDto;
import com.prgrmsfinal.skypedia.member.dto.MemberResponseDto;
import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.exception.InvalidSocialTypeException;
import com.prgrmsfinal.skypedia.member.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SocialLoginServiceImpl implements SocialLoginService {
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public SocialLoginServiceImpl(MemberService memberService
            , JwtTokenUtil jwtTokenUtil
            , RefreshTokenService refreshTokenService
    ) {
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public MemberResponseDto.Login authenticate(Map<String, Object> attributes, SocialType socialType) {
        MemberInternalDto.Create createDto = switch (socialType) {
            case NAVER -> ofNaver(attributes);
            case KAKAO -> ofKakao(attributes);
            case GOOGLE -> ofGoogle(attributes);
            default -> throw new InvalidSocialTypeException();
        };

        try {
            MemberInternalDto.Profile profileDto = memberService.getProfileByOAuthId(createDto.oauthId());

            String accessToken = jwtTokenUtil.createAccessToken(profileDto.id(), profileDto.roleTypes());

            String refreshToken = refreshTokenService.get(profileDto.id());

            if (refreshToken != null) {
                refreshTokenService.delete(profileDto.id());
            }

            refreshTokenService.save(profileDto.id(), jwtTokenUtil.createRefreshToken(profileDto.id()));

            return MemberResponseDto.Login.builder()
                    .nickname(profileDto.nickname())
                    .accessToken(accessToken)
                    .profilePhotoUrl(profileDto.profilePhotoUrl())
                    .build();
        } catch (NoSuchElementException e) {
            Member member = memberService.create(createDto);

            String accessToken = jwtTokenUtil.createAccessToken(member.getId(), List.of(RoleType.USER));

            refreshTokenService.save(member.getId(), jwtTokenUtil.createRefreshToken(member.getId()));

            return MemberResponseDto.Login.builder()
                    .nickname(member.getNickname())
                    .accessToken(accessToken)
                    .profilePhotoUrl(null)
                    .build();
        }
    }

    private MemberInternalDto.Create ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return MemberInternalDto.Create.builder()
                .oauthId((String) response.get("id"))
                .name((String) response.get("name"))
                .nickname(memberService.generateRandomNickname())
                .email((String) response.get("email"))
                .build();
    }

    private MemberInternalDto.Create ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

        return MemberInternalDto.Create.builder()
                .oauthId((String) attributes.get("id"))
                .name((String) account.get("name"))
                .nickname(memberService.generateRandomNickname())
                .email((String) account.get("email"))
                .build();
    }

    private MemberInternalDto.Create ofGoogle(Map<String, Object> attributes) {
        return MemberInternalDto.Create.builder()
                .oauthId((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .nickname(memberService.generateRandomNickname())
                .email((String) attributes.get("email"))
                .build();
    }
}
