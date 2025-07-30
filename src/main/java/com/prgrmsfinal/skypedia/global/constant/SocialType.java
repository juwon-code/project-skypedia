package com.prgrmsfinal.skypedia.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.prgrmsfinal.skypedia.member.exception.InvalidSocialTypeException;
import java.util.Arrays;

public enum SocialType {
    NAVER, KAKAO, GOOGLE;

    @Override
    public String toString() {
        return this.name();
    }

    @JsonCreator
    public static SocialType fromString(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(name))
                .findFirst()
                .orElseThrow(InvalidSocialTypeException::new);
    }
}