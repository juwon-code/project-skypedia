package com.prgrmsfinal.skypedia.global.dto;

import com.prgrmsfinal.skypedia.global.annotation.EnumValid;
import com.prgrmsfinal.skypedia.global.constant.SearchOption;
import com.prgrmsfinal.skypedia.global.constant.SortType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public sealed interface SearchRequestDto permits SearchRequestDto.Member {
    record Member(
            @EnumValid(enumClass = SearchOption.class
                    , includeOnly = {"MEMBER_NICKNAME", "MEMBER_EMAIL", "MEMBER_ALL"}
                    , message = "사용할 수 없는 검색 옵션입니다."
            )
            SearchOption searchOption,

            @EnumValid(enumClass = SortType.class
                    , includeOnly = {"RELEVANCE", "OLDEST", "NEWEST"}
                    , message = "사용할 수 없는 정렬 옵션입니다.")
            SortType sortType,

            @NotNull(message = "검색 키워드는 비워둘 수 없습니다.")
            String keyword,

            @Min(value = 1, message = "페이지는 0 또는 음수값이 될 수 없습니다.")
            int page
    ) implements SearchRequestDto {
        @Builder
        public Member {}
    }
}
