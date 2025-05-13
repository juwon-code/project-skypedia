package com.prgrmsfinal.skypedia.votepost.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum VotePosition {
    FIRST(1), SECOND(2);

    private final int value;

    VotePosition(int value) {
        this.value = value;
    }

    public static VotePosition getInstance(int value) {
        return Arrays.stream(values())
                .filter(pos -> pos.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("정의되지 않은 포지션 값이 감지되었습니다."));
    }
}
