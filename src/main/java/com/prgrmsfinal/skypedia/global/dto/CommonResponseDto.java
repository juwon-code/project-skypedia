package com.prgrmsfinal.skypedia.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.util.Map;

public sealed interface CommonResponseDto permits CommonResponseDto.ApiSuccess, CommonResponseDto.ApiError {
    record ApiSuccess<T>(
            @NotBlank(message = "응답 메시지는 비우거나 공백일 수 없습니다.")
            @Size(min = 5, max = 255, message = "응답 메시지는 5 ~ 255자까지 허용됩니다.")
            String message,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            T result
    ) implements CommonResponseDto {
        @Builder
        public ApiSuccess {}
    }

    record ApiError(
            @NotBlank(message = "에러 메시지는 비우거나 공백일 수 없습니다.")
            @Size(min = 5, max = 255, message = "에러 메시지는 5 ~ 255자까지 허용됩니다.")
            String message,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            Map<String, String> details
    ) implements CommonResponseDto {
        @Builder
        public ApiError {}
    }
}
