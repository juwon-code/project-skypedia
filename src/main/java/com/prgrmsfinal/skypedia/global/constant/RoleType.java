package com.prgrmsfinal.skypedia.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.prgrmsfinal.skypedia.member.exception.InvalidRoleTypeException;
import java.util.Arrays;

public enum RoleType {
    USER, ADMIN;

    @Override
    public String toString() {
        return this.name();
    }

    @JsonCreator
    public static RoleType fromJson(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equals(name))
                .findFirst()
                .orElseThrow(InvalidRoleTypeException::new);
    }
}
