package com.prgrmsfinal.skypedia.member.dto;

import com.prgrmsfinal.skypedia.global.constant.SocialType;
import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

public sealed interface MemberRequestDto permits MemberRequestDto.SocialInfo, MemberRequestDto.SearchOptions,
        MemberRequestDto.Modify, MemberRequestDto.ChangeRole {
    record SocialInfo(
            @NotNull(message = "소셜 식별 ID는 비워둘 수 없습니다.")
            String oauthId,

            @NotBlank(message = "성명은 비워둘 수 없습니다.")
            String name,

            @NotNull(message = "이메일은 비워둘 수 없습니다.")
            @Email(message = "이메일의 형식에 맞지 않습니다.")
            String email,

            SocialType socialType
    ) implements MemberRequestDto {
        @Builder
        public SocialInfo {}
    }

    record SearchOptions(
            Long memberId,

            String oauthId,

            Boolean removed
    ) implements MemberRequestDto {
        @Builder
        public SearchOptions {}
    }

    record Modify(
            @Length(min = 2, max = 20, message = "닉네임은 2 ~ 20자 길이여야 합니다.")
            @Pattern(regexp = "^[^\\s]+$", message = "닉네임은 공백을 포함될 수 없습니다.")
            String nickname,

            @Valid
            PhotoRequestDto.Upload photoData
    ) implements MemberRequestDto {
        @Builder
        public Modify {}
    }

    record ChangeRole(
            @NotBlank(message = "변경할 역할명을 입력해야 합니다.")
            String roleName,

            @NotBlank(message = "올바른 명령어를 입력해야 합니다. (ADD/DEL)")
            @Pattern(regexp = "ADD|DEL", message = "유효한 명령어는 ADD 또는 DEL입니다.")
            String command
    ) implements MemberRequestDto {
        @Builder
        public ChangeRole {}
    }
}
