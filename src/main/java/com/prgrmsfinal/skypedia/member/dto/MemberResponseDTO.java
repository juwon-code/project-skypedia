package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public sealed interface MemberResponseDto permits MemberResponseDto.Info, MemberResponseDto.SimpleProfile
        , MemberResponseDto.Profile, MemberResponseDto.SearchProfile, MemberResponseDto.SignIn, MemberResponseDto.Modify
        , MemberResponseDto.Remove {
    record Info(
            Long id,
            String nickname,
            String photoUrl,
            List<RoleType> roleTypes
    ) implements MemberResponseDto {
        @Builder
        public Info {}
    }

    record SimpleProfile(
            String nickname,
            String photoUrl
    ) implements MemberResponseDto {
        @Builder
        public SimpleProfile {}
    }

    record Profile(
            String nickname,
            String email,
            SocialType socialType,
            String photoUrl,
            boolean removed
    ) implements MemberResponseDto {
        @Builder
        public Profile {}
    }

    record SearchProfile(
            Long id,
            String nickname,
            String email,
            String socialType,
            String photoUrl,
            List<String> roleTypes,
            LocalDateTime createdAt,
            LocalDateTime removedAt
    ) implements MemberResponseDto {
        @Builder
        public SearchProfile {}
    }

    record SignIn(
            String nickname,
            String photoUrl,
            String accessToken
    ) implements MemberResponseDto {
        @Builder
        public SignIn {}
    }

    record Modify(
            String nickname,
            String uuid,
            String uploadUrl
    ) implements MemberResponseDto {
        @Builder
        public Modify {}
    }

    record Remove(
            LocalDateTime removedAt,
            LocalDateTime willDeletedAt
    ) implements MemberResponseDto {
        @Builder
        public Remove {}
    }
}
