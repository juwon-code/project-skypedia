package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.global.constant.RoleType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public sealed interface MemberInternalDto permits MemberInternalDto.Create, MemberInternalDto.Profile {
    record Create(
            @NotNull(message = "소셜 식별 ID는 비워둘 수 없습니다.")
            String oauthId,

            @NotBlank(message = "성명은 비워둘 수 없습니다.")
            String name,

            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @NotNull(message = "닉네임은 비워둘 수 없습니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함할 수 없습니다.")
            String nickname,

            @NotNull(message = "이메일은 비워둘 수 없습니다.")
            @Email(message = "이메일의 형식에 맞지 않습니다.")
            String email
    ) implements MemberInternalDto {
        @Builder
        public Create {}
    }

    record Profile(
            @NotNull(message = "회원 ID는 비워둘 수 없습니다.")
            @Min(value = 0, message = "회원 ID는 0 이상의 양수값이어야 합니다.")
            Long id,

            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @NotNull(message = "닉네임은 비워둘 수 없습니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함할 수 없습니다.")
            String nickname,

            @URL(message = "프로필 사진 링크가 올바른 URL 형식이 아닙니다.")
            String profilePhotoUrl,

            @NotNull(message = "최소 하나의 역할을 가지고 있어야 합니다.")
            List<RoleType> roleTypes
    ) implements MemberInternalDto {
        @Builder
        public Profile {}
    }
}
