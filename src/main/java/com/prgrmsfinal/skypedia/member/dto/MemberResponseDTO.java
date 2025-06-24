package com.prgrmsfinal.skypedia.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrmsfinal.skypedia.global.constant.SocialType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDateTime;

public sealed interface MemberResponseDto permits MemberResponseDto.ReadProfile, MemberResponseDto.SimpleProfile
        , MemberResponseDto.ChangeProfile, MemberResponseDto.Withdraw, MemberResponseDto.Login {
    record ReadProfile(
            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함될 수 없습니다.")
            String nickname,

            @NotNull(message = "이메일은 비워둘 수 없습니다.")
            @Email(message = "이메일 형식과 일치하지 않습니다.")
            String email,

            @NotNull(message = "소셜 계정 형식은 비워둘 수 없습니다.")
            SocialType socialType,

            @URL(message = "프로필 사진 링크가 올바른 URL 형식이 아닙니다.")
            String profilePhotoUrl
    ) implements MemberResponseDto {
        @Builder
        public ReadProfile {}
    }

    record SimpleProfile(
            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함될 수 없습니다.")
            String nickname,

            @URL(message = "프로필 사진 링크가 올바른 URL 형식이 아닙니다.")
            String profilePhotoUrl
    ) implements MemberResponseDto {
        @Builder
        public SimpleProfile {}
    }

    record ChangeProfile(
            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함할 수 없습니다.")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String nickname,

            @UUID(message = "올바른 UUID 형태가 아닙니다.")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String uuid,

            @URL(message = "프로필 사진 업로드 링크는 URL 형식이어야 합니다.")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String uploadUrl
    ) implements MemberResponseDto {
        @Builder
        public ChangeProfile {}
    }

    record Withdraw(
            @NotNull(message = "삭제한 날짜는 비워둘 수 없습니다.")
            LocalDateTime removedAt,

            @NotNull(message = "영구 삭제되는 날짜는 비워둘 수 없습니다.")
            LocalDateTime willDeletedAt
    ) implements MemberResponseDto {
        @Builder
        public Withdraw {}
    }

    record Login(
            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함될 수 없습니다.")
            String nickname,

            @URL(message = "프로필 사진 링크가 올바른 URL 형식이 아닙니다.")
            String profilePhotoUrl,

            @NotNull(message = "인증 토큰은 반드시 발급되어야 합니다.")
            String accessToken
    ) implements MemberResponseDto {
        @Builder
        public Login {}
    }
}
