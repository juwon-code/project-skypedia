package com.prgrmsfinal.skypedia.global.dto;

import lombok.Builder;

import java.util.List;

public sealed interface SearchResponseDto permits SearchResponseDto.Pagination, SearchResponseDto.Slice {
    record Pagination<T>(
            int page,

            int totalPages,

            long totalCount,

            boolean isFirst,

            boolean isLast,

            List<T> data
    ) implements SearchResponseDto {
        @Builder
        public Pagination {}
    }

    record Slice<T>(
            boolean hasNext,

            int page,

            List<T> data
    ) implements SearchResponseDto {
        @Builder
        public Slice {}
    }
}
