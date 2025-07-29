package com.prgrmsfinal.skypedia.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.util.Map;

public sealed interface CommonResponseDto permits CommonResponseDto.ApiSuccess, CommonResponseDto.ApiError {
    record ApiSuccess<T>(
            String message,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            T result
    ) implements CommonResponseDto {
        @Builder
        public ApiSuccess {}
    }

    record ApiError(
            String message,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            Map<String, String> details
    ) implements CommonResponseDto {
        @Builder
        public ApiError {}
    }
}
